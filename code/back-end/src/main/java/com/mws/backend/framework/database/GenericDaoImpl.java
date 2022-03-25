package com.mws.backend.framework.database;


import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.metadata.ConstraintDescriptor;
import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.stream.Collectors;


public abstract class GenericDaoImpl<EntityClass, Id extends Serializable> implements GenericDao<EntityClass, Id> {

    @PersistenceContext
    protected EntityManager entityManager;
    private final Session session;
    private final Class<EntityClass> entityClass;

    public GenericDaoImpl() {
        Type type = getClass().getGenericSuperclass();
        ParameterizedType pt = (ParameterizedType) type;
        this.entityClass = (Class) pt.getActualTypeArguments()[0];
        session = (Session) entityManager.getDelegate();
    }

    private Session getSession() {
        if (session == null)
            throw new IllegalStateException("Session has not been set on DAO before usage");
        return session;
    }

    protected Class<EntityClass> getEntityClass() {
        return entityClass;
    }

    @Override
    @Transactional
    public EntityClass create(final EntityClass entity) {
        final List<String> violatedConstraintMessages = getViolatedConstraints(entity);

        if (!violatedConstraintMessages.isEmpty()) {
            throw new RuntimeException(violatedConstraintMessages.toString());
        }

        this.entityManager.persist(entity);
        return entity;
    }

    private List<String> getViolatedConstraints(EntityClass entity) {
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        return validator.validate(entity).stream().map(ConstraintViolation::getConstraintDescriptor)
                .map(ConstraintDescriptor::getMessageTemplate).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void update(final EntityClass t) {
        this.entityManager.merge(t);
    }

    @Override
    public EntityClass findById(final Id id) {
        return this.entityManager.find(entityClass, id);
    }

    @Override
    @Transactional
    public void delete(final Id id) {
        this.entityManager.remove(this.entityManager.getReference(entityClass, id));
    }

    /**
     * Use this inside subclasses as a convenience method.
     */
    @SuppressWarnings("unchecked")
    protected List<EntityClass> findByCriteria(Criterion... criterion) {
        //getSession().getCriteriaBuilder()
        Criteria crit = getCriteria();
        for (Criterion c : criterion) {
            crit.add(c);
        }
        return crit.list();
    }

    private Criteria getCriteria() {
        return getSession().createCriteria(getEntityClass());
    }

}
