package com.mws.backend.framework.database;


import com.mws.backend.framework.exception.EntityNotFound;
import com.mws.backend.framework.exception.EntityPersistenceException;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
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
import java.util.stream.Collectors;

import static com.mws.backend.framework.exception.EntityPersistenceException.toDatabaseException;
import static org.springframework.util.CollectionUtils.isEmpty;


public abstract class GenericDaoImpl<Entity, Id> implements GenericDao<Entity, Id> {

    @PersistenceContext
    protected EntityManager entityManager;
    private final Class<Entity> entityClass;

    public static final String COLUMN_ID = "id";

    public GenericDaoImpl() {
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
                .map(ConstraintDescriptor::getMessageTemplate).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void update(final Entity t) throws EntityPersistenceException {
        try {
            this.entityManager.merge(t);
        } catch (PersistenceException e) {
            throw toDatabaseException(e);
        }
    }

    /**
     * Force entity search in database.
     *
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
    public List<Entity> findAll(final Collection<Id> ids) {
        if (isEmpty(ids)) {
            return Collections.emptyList();
        }

        Class<Entity> entityClass = getEntityClass();
        CriteriaQuery<Entity> criteriaQuery = getCriteriaBuilder().createQuery(entityClass);
        Root<Entity> root = criteriaQuery.from(entityClass);
        criteriaQuery.where(root.get(COLUMN_ID).in(ids));

        return entityManager.createQuery(criteriaQuery).getResultList();
    }

    /**
     * Find entity in database or return null.
     */
    @Override
    public Entity findWeak(final Id id) {
        return this.entityManager.find(entityClass, id);
    }

    @Override
    public List<Entity> findBy(final String columnName, final String value, final Integer maxResults) {
        Class<Entity> entityClass = getEntityClass();
        CriteriaQuery<Entity> criteriaQuery = getCriteriaBuilder().createQuery(entityClass);
        Root<Entity> root = criteriaQuery.from(entityClass);
        criteriaQuery.where(getCriteriaBuilder().equal(root.get(columnName), value));

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
