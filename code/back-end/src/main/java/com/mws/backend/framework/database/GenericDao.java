package com.mws.backend.framework.database;

import com.mws.backend.framework.exception.EntityPersistenceException;

public interface GenericDao<EntityClass, Id> {
    EntityClass create(EntityClass entity) throws EntityPersistenceException;

    void update(EntityClass entity);

    EntityClass find(Id id);

    EntityClass findWeak(final Id id);

    void delete(Id id);

}