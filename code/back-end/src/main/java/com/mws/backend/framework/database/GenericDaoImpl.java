package com.mws.backend.framework.database;


import com.mws.backend.framework.exception.EntityNotFound;
import com.mws.backend.framework.exception.EntityPersistenceException;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
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

    private final Class<EntityClass> type;

    public GenericDaoImpl() {
        Type type = getClass().getGenericSuperclass();
        ParameterizedType pt = (ParameterizedType) type;
        this.type = (Class) pt.getActualTypeArguments()[0];
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
            this.entityManager.flush();
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
    @Transactional
    public void delete(final Id id) {
        this.entityManager.remove(this.entityManager.getReference(type, id));
    }

}
