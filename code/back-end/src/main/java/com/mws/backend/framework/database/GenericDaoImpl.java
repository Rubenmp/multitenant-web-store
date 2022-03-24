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


// https://www.codeproject.com/Articles/251166/The-Generic-DAO-Pattern-in-Java-with-Spring-and-JP
public abstract class GenericDaoImpl<T, Id> implements GenericDao<T, Id> {

    @PersistenceContext
    protected EntityManager em;

    private final Class<T> type;

    public GenericDaoImpl() {
        Type t = getClass().getGenericSuperclass();
        ParameterizedType pt = (ParameterizedType) t;
        type = (Class) pt.getActualTypeArguments()[0];
    }

    @Override
    @Transactional
    public T create(final T t) {
        List<String> violatedConstraintMessages = validate(t);

        if (!violatedConstraintMessages.isEmpty()) {
            throw new RuntimeException(violatedConstraintMessages.toString());
        }

        this.em.persist(t);
        return t;
    }

    private List<String> validate(T t) {
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        return validator.validate(t).stream().map(ConstraintViolation::getConstraintDescriptor).map(ConstraintDescriptor::getMessageTemplate).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void delete(final Id id) {
        this.em.remove(this.em.getReference(type, id));
    }

    @Override
    public T find(final Id id) {
        return (T) this.em.find(type, id);
    }

    @Override
    @Transactional
    public T update(final T t) {
        return this.em.merge(t);
    }
}
