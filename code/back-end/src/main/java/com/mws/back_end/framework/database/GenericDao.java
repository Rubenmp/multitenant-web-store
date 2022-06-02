package com.mws.back_end.framework.database;

import com.mws.back_end.framework.exception.EntityPersistenceException;

import java.util.List;

public interface GenericDao<Entity, Id> {
    Entity create(Entity entity) throws EntityPersistenceException;

    void update(Entity entity);

    Entity find(Id id);
    Entity findWeak(final Id id);
    List<Entity> findBy(String columnName, String value, Integer maxResults);

    void delete(Id id);
}