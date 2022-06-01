package com.mws.back_end.framework.database;


import com.mws.back_end.account.service.security.JwtCipher;
import com.mws.back_end.framework.exception.EntityNotFound;
import com.mws.back_end.framework.exception.EntityPersistenceException;
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

    @Override
    @Transactional
    public Entity create(final Entity entity) throws EntityPersistenceException {
        final List<String> violatedConstraintMessages = getViolatedConstraints(entity);

        if (!violatedConstraintMessages.isEmpty()) {
            throw new RuntimeException(violatedConstraintMessages.toString());
        }

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

    @Override
    @Transactional
    public void update(final Entity t) throws EntityPersistenceException {
        final Long tenantId = jwtCipher.getCurrentTenantId();
        requireNotNull(tenantId, "Tenant info must be provided");
        try {
            this.entityManager.merge(t);
        } catch (PersistenceException e) {
            throw toDatabaseException(e);
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

    @Override
    public List<Entity> find(final Collection<Id> ids, final Boolean active) {
        if (ids != null && ids.isEmpty()) {
            return Collections.emptyList();
        }

        Class<Entity> entityClass = getEntityClass();
        CriteriaQuery<Entity> criteriaQuery = getCriteriaBuilder().createQuery(entityClass);
        Root<Entity> root = criteriaQuery.from(entityClass);
        Predicate predicate = getCriteriaBuilder().conjunction();

        if (ids != null && !ids.isEmpty()) {
            predicate = getCriteriaBuilder().and(predicate, root.get(DB_COLUMN_ID).in(ids));
        }

        if (active != null) {
            predicate = getCriteriaBuilder().and(predicate, root.get(DB_COLUMN_ACTIVE).in(List.of(active)));
        }

        final Long tenantId = jwtCipher.getCurrentTenantId();
        if (tenantId != null) {
            predicate = getCriteriaBuilder().and(predicate, getCriteriaBuilder().equal(root.get(DB_COLUMN_TENANT_ID), tenantId));
        }
        criteriaQuery.where(predicate);

        return entityManager.createQuery(criteriaQuery).getResultList();
    }


    /**
     * Find entity in database or return null.
     */
    @Override
    public Entity findWeak(final Id id) {
        Class<Entity> entityClass = getEntityClass();
        CriteriaQuery<Entity> criteriaQuery = getCriteriaBuilder().createQuery(entityClass);
        Root<Entity> root = criteriaQuery.from(entityClass);
        Predicate predicate = getCriteriaBuilder().conjunction();

        final Long tenantId = jwtCipher.getCurrentTenantId();
        if (tenantId != null) {
            predicate = getCriteriaBuilder().and(predicate, getCriteriaBuilder().equal(root.get(DB_COLUMN_TENANT_ID), tenantId));
        }
        predicate = getCriteriaBuilder().and(predicate, getCriteriaBuilder().equal(root.get(DB_COLUMN_ID), id));

        criteriaQuery.where(predicate);

        return entityManager.createQuery(criteriaQuery).getResultList().stream().findFirst().orElse(null);
    }

    @Override
    public List<Entity> findBy(final String columnName, final String value, final Integer maxResults) {
        Class<Entity> entityClass = getEntityClass();
        CriteriaQuery<Entity> criteriaQuery = getCriteriaBuilder().createQuery(entityClass);
        Root<Entity> root = criteriaQuery.from(entityClass);

        Predicate predicate = getCriteriaBuilder().conjunction();
        final Long tenantId = jwtCipher.getCurrentTenantId();
        if (tenantId != null) {
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
        this.entityManager.remove(this.entityManager.getReference(entityClass, id));
    }

}
