package com.mws.backend.framework.database;


import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.metadata.ConstraintDescriptor;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.stream.Collectors;


public class GenericDaoImpl<EntityClass, Id> implements GenericDao<EntityClass, Id> {

    @PersistenceContext
    protected EntityManager entityManager;

    private final Class<EntityClass> type;

    public GenericDaoImpl() {
        Type type = getClass().getGenericSuperclass();
        ParameterizedType pt = (ParameterizedType) type;
        this.type = (Class) pt.getActualTypeArguments()[0];
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
    public EntityClass update(final EntityClass t) {
        return this.entityManager.merge(t);
    }

    @Override
    public EntityClass find(final Id id) {
        return this.entityManager.find(type, id);
    }

    @Override
    @Transactional
    public void delete(final Id id) {
        this.entityManager.remove(this.entityManager.getReference(type, id));
    }

}