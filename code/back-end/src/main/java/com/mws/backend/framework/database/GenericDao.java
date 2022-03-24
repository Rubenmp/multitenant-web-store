package com.mws.backend.framework.database;

import com.mws.backend.framework.database.exception.EntityPersistenceException;

public interface GenericDao<EntityClass, Id> {
    EntityClass create(EntityClass entity) throws EntityPersistenceException;

    EntityClass update(EntityClass entity);

    EntityClass find(Id id);

    void delete(Id id);

}