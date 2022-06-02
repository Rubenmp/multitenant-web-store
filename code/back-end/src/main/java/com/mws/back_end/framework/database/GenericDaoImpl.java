package com.mws.back_end.framework.database;


import com.mws.back_end.account.interfaces.user.dto.UserRoleDto;
import com.mws.back_end.account.model.entity.Tenant;
import com.mws.back_end.account.model.entity.User;
import com.mws.back_end.account.model.entity.UserRole;
import com.mws.back_end.account.service.security.JwtCipher;
import com.mws.back_end.framework.exception.EntityNotFound;
import com.mws.back_end.framework.exception.EntityPersistenceException;
import com.mws.back_end.framework.exception.MWSRException;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.metadata.ConstraintDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static com.mws.back_end.framework.exception.EntityPersistenceException.toDatabaseException;
import static com.mws.back_end.framework.utils.ExceptionUtils.requireNotNull;


public abstract class GenericDaoImpl<Entity, Id> implements GenericDao<Entity, Id> {

    @PersistenceContext
    protected EntityManager entityManager;
    private final Class<Entity> entityClass;

    protected static final String DB_COLUMN_TENANT_ID = "tenantId";
    protected static final String DB_COLUMN_ID = "id";
    protected static final String DB_COLUMN_ACTIVE = "active";

    @Autowired
    private JwtCipher jwtCipher;

    protected GenericDaoImpl() {
        Type type = getClass().getGenericSuperclass();
        ParameterizedType pt = (ParameterizedType) type;
        this.entityClass = (Class) pt.getActualTypeArguments()[0];
    }

    private Class<Entity> getEntityClass() {
        return entityClass;
    }

    @Data
    @NoArgsConstructor
    protected class DBSearch {
        private Collection<Id> ids;
        private Boolean active;
        private Integer maxResults;
    }

    @Override
    @Transactional
    public Entity create(final Entity entity) throws EntityPersistenceException {
        final List<String> violatedConstraintMessages = getViolatedConstraints(entity);

        if (!violatedConstraintMessages.isEmpty()) {
            throw new MWSRException(violatedConstraintMessages.toString());
        }

        checkTenantPermissionsToCreateEntity(entity);
        try {
            this.entityManager.persist(entity);
        } catch (PersistenceException e) {
            throw toDatabaseException(e);
        }
        return entity;
    }

    private List<String> getViolatedConstraints(Entity entity) {
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        return validator.validate(entity).stream().map(ConstraintViolation::getConstraintDescriptor)
                .map(ConstraintDescriptor::getMessageTemplate).toList();
    }

    private void checkTenantPermissionsToCreateEntity(final Entity entity) {
        if (!jwtCipher.jwtRestrictionsEnabled()) {
            return;
        }
        if (entity instanceof Tenant) {
            if (UserRoleDto.SUPER != jwtCipher.getCurrentUserRole()) {
                throw new EntityPersistenceException("It's not possible to create tenants.");
            }
        } else if (entity instanceof User userToCreate) {
            final UserRoleDto userRole = jwtCipher.getCurrentUserRole();
            if ((UserRole.SUPER == userToCreate.getRole() && UserRoleDto.SUPER != userRole)
                    || (UserRole.ADMIN == userToCreate.getRole() && (UserRoleDto.SUPER != userRole && UserRoleDto.ADMIN != userRole))) {
                throw new EntityPersistenceException("It's not possible to create user.");
            }
        } else {
            final Long tokenTenantId = jwtCipher.getCurrentTenantId();
            requireNotNull(tokenTenantId, "Tenant info must be provided");

            final Long entityTenantId = getTenantId(entity);
            if (entityTenantId == null || !entityTenantId.equals(tokenTenantId)) {
                throw new EntityPersistenceException("It's not possible to update entity in other tenants.");
            }
        }
    }


    @Override
    @Transactional
    public void update(final Entity entity) throws EntityPersistenceException {
        checkTenantPermissionsToUpdateEntity(entity);
        try {
            this.entityManager.merge(entity);
        } catch (PersistenceException e) {
            throw toDatabaseException(e);
        }
    }

    private void checkTenantPermissionsToUpdateEntity(final Entity entity) {
        if (!jwtCipher.jwtRestrictionsEnabled()) {
            return;
        }
        final Long tokenTenantId = jwtCipher.getCurrentTenantId();
        requireNotNull(tokenTenantId, "Tenant info must be provided");

        if (!(entity instanceof Tenant)) {
            final Long entityTenantId = getTenantId(entity);
            if (entityTenantId == null || !entityTenantId.equals(tokenTenantId)) {
                throw new EntityPersistenceException("It's not possible to update entity in other tenants.");
            }
        }
    }

    private Long getTenantId(final Entity t) {
        try {
            final Method method = t.getClass().getMethod("getTenantId");
            return (Long) method.invoke(t);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            return null;
        }
    }

    /**
     * Force entity search in database.
     * <p>
     * Runtime exception:
     * - EntityNotFound if there is no entity with provided id
     */
    @Override
    public Entity find(final Id id) {
        final Entity entity = findWeak(id);
        if (entity == null) {
            throw new EntityNotFound(entityClass.toString() + " entity with id " + id + " does not exist.");
        }

        return entity;
    }

    protected List<Entity> find(final DBSearch dbSearch) {
        requireNotNull(dbSearch, "Required database search.");
        Collection<Id> ids = dbSearch.getIds();
        if (ids != null && ids.isEmpty()) {
            return Collections.emptyList();
        }

        CriteriaQuery<Entity> criteriaQuery = getCriteriaBuilder().createQuery(getEntityClass());
        Root<Entity> root = criteriaQuery.from(getEntityClass());
        Predicate predicate = getCriteriaBuilder().conjunction();

        if (ids != null && !ids.isEmpty()) {
            predicate = getCriteriaBuilder().and(predicate, root.get(DB_COLUMN_ID).in(ids));
        }

        if (dbSearch.getActive() != null) {
            predicate = getCriteriaBuilder().and(predicate, root.get(DB_COLUMN_ACTIVE).in(List.of(dbSearch.getActive())));
        }

        final Long tenantId = jwtCipher.getCurrentTenantId();
        if (tenantId != null && jwtCipher.jwtRestrictionsEnabled()) {
            predicate = getCriteriaBuilder().and(predicate, getCriteriaBuilder().equal(root.get(DB_COLUMN_TENANT_ID), tenantId));
        }
        criteriaQuery.where(predicate);

        final TypedQuery<Entity> query = entityManager.createQuery(criteriaQuery);
        if (dbSearch.getMaxResults() != null) {
            return query.setMaxResults(dbSearch.getMaxResults()).getResultList();
        }

        return query.getResultList();
    }


    /**
     * Find entity in database or return null.
     */
    @Override
    public Entity findWeak(final Id id) {
        CriteriaQuery<Entity> criteriaQuery = getCriteriaBuilder().createQuery(getEntityClass());
        Root<Entity> root = criteriaQuery.from(getEntityClass());
        Predicate predicate = getCriteriaBuilder().conjunction();

        final Long tenantId = jwtCipher.getCurrentTenantId();
        if (tenantId != null && jwtCipher.jwtRestrictionsEnabled()) {
            predicate = getCriteriaBuilder().and(predicate, getCriteriaBuilder().equal(root.get(DB_COLUMN_TENANT_ID), tenantId));
        }
        predicate = getCriteriaBuilder().and(predicate, getCriteriaBuilder().equal(root.get(DB_COLUMN_ID), id));

        criteriaQuery.where(predicate);

        return entityManager.createQuery(criteriaQuery).getResultList().stream().findFirst().orElse(null);
    }

    @Override
    public List<Entity> findBy(final String columnName, final String value, final Integer maxResults) {
        CriteriaQuery<Entity> criteriaQuery = getCriteriaBuilder().createQuery(getEntityClass());
        Root<Entity> root = criteriaQuery.from(getEntityClass());

        Predicate predicate = getCriteriaBuilder().conjunction();
        final Long tenantId = jwtCipher.getCurrentTenantId();
        if (tenantId != null && jwtCipher.jwtRestrictionsEnabled()) {
            predicate = getCriteriaBuilder().and(predicate, getCriteriaBuilder().equal(root.get(DB_COLUMN_TENANT_ID), tenantId));
        }
        predicate = getCriteriaBuilder().and(predicate, getCriteriaBuilder().equal(root.get(columnName), value));
        criteriaQuery.where(predicate);

        TypedQuery<Entity> query = entityManager.createQuery(criteriaQuery);
        if (maxResults != null) {
            return query.setMaxResults(maxResults).getResultList();
        }

        return query.getResultList();
    }

    private CriteriaBuilder getCriteriaBuilder() {
        return entityManager.getCriteriaBuilder();
    }

    @Override
    @Transactional
    public void delete(final Id id) {
        // Warning: only entities with "active" field are allowed to be deleted logically
        final Entity reference = this.entityManager.getReference(entityClass, id);
        checkTenantPermissionsToUpdateEntity(reference);

        try {
            final Method method = reference.getClass().getMethod("setActive", boolean.class);
            method.invoke(reference, false);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new EntityPersistenceException("It's not possible to delete entity with id " + id);
        }

        this.entityManager.remove(reference);
    }

}
