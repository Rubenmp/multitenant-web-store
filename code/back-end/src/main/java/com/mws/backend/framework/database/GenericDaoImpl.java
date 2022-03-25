package com.mws.backend.framework.database;


import com.mws.backend.framework.exception.EntityNotFound;
import com.mws.backend.framework.exception.EntityPersistenceException;
import org.hibernate.Session;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
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
import java.util.List;
import java.util.stream.Collectors;

import static com.mws.backend.framework.exception.EntityPersistenceException.toDatabaseException;


public abstract class GenericDaoImpl<EntityClass, Id> implements GenericDao<EntityClass, Id> {

    @PersistenceContext
    protected EntityManager entityManager;
    private Session session; // It can be used to create custom queries
    private final Class<EntityClass> type;

    public GenericDaoImpl() {
        Type type = getClass().getGenericSuperclass();
        ParameterizedType pt = (ParameterizedType) type;
        this.type = (Class) pt.getActualTypeArguments()[0];
    }

    @PostConstruct
    public void initSession(){
        session = (Session) entityManager.getDelegate();
    }

    private Class<EntityClass> getEntityClass() {
        return type;
    }

    private Session getSession() {
        if (session == null)
            throw new IllegalStateException("Session has not been set on DAO before usage");
        return session;
    }

    @Override
    @Transactional
    public EntityClass create(final EntityClass entity) throws EntityPersistenceException {
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

    private List<String> getViolatedConstraints(EntityClass entity) {
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        return validator.validate(entity).stream().map(ConstraintViolation::getConstraintDescriptor)
                .map(ConstraintDescriptor::getMessageTemplate).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void update(final EntityClass t) throws EntityPersistenceException {
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
    public EntityClass find(final Id id) {
        final EntityClass entity = findWeak(id);
        if (entity == null) {
            throw new EntityNotFound(type.toString() + " entity with id " + id + " does not exist.");
        }

        return entity;
    }

    /**
     * Find entity in database or return null.
     */
    @Override
    public EntityClass findWeak(final Id id) {
        return this.entityManager.find(type, id);
    }

    @Override
    public List<EntityClass> findBy(final String columnName, final String value) {
        Class<EntityClass> entityClass = getEntityClass();
        CriteriaQuery<EntityClass> criteriaQuery = getCriteriaBuilder().createQuery(entityClass);
        Root<EntityClass> root = criteriaQuery.from(entityClass);
        criteriaQuery.where(getCriteriaBuilder().equal(root.get(columnName), value));

        return entityManager.createQuery(criteriaQuery).getResultList();
    }

    private CriteriaBuilder getCriteriaBuilder() {
        return entityManager.getCriteriaBuilder();
    }

    @Override
    @Transactional
    public void delete(final Id id) {
        this.entityManager.remove(this.entityManager.getReference(type, id));
    }

}
