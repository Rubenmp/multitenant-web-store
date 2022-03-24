package com.mws.backend.framework.database;

public interface GenericDao<EntityClass, Id> {
    EntityClass create(EntityClass entity);

    EntityClass update(EntityClass entity);

    EntityClass find(Id id);

    void delete(Id id);

}