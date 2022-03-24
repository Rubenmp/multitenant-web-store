package com.mws.backend.framework.database;

public interface GenericDao<EntityClass, Id> {
    EntityClass create(EntityClass entity);

    void delete(Id id);

    EntityClass find(Id id);

    EntityClass update(EntityClass entity);
}