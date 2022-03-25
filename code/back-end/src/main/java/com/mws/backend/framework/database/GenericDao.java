package com.mws.backend.framework.database;

import java.io.Serializable;


public interface GenericDao<EntityClass, Id extends Serializable> {

    EntityClass create(final EntityClass entity);

    EntityClass findById(Id id);

    void update(final EntityClass t);

    //EntityClass makePersistent(EntityClass entity);

    //void makeTransient(EntityClass entity);

    public void delete(final Id id);
}