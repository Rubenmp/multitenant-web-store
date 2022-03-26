package com.mws.backend.framework.database;

import com.mws.backend.framework.exception.EntityPersistenceException;

import java.util.List;

public interface GenericDao<EntityClass, Id> {
    EntityClass create(EntityClass entity) throws EntityPersistenceException;

    void update(EntityClass entity);

    EntityClass find(Id id);
    EntityClass findWeak(final Id id);
    List<EntityClass> findBy(String columnName, String value, Integer maxResults);

    void delete(Id id);

}