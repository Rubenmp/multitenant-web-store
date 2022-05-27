package com.mws.back_end.framework.database;

import com.mws.back_end.framework.exception.EntityPersistenceException;

import java.util.Collection;
import java.util.List;

public interface GenericDao<Entity, Id> {
    Entity create(Entity entity) throws EntityPersistenceException;

    void update(Entity entity);

    Entity find(Id id);

    List<Entity> find(Collection<Id> ids, Boolean active);

    List<Entity> findAll(Collection<Id> ids);
    Entity findWeak(final Id id);
    List<Entity> findBy(String columnName, String value, Integer maxResults);

    void delete(Id id);

}