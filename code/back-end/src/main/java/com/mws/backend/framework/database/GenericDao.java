package com.mws.backend.framework.database;

import java.io.Serializable;
import java.util.List;


public interface GenericDao<EntityClass, ID extends Serializable> {
    EntityClass findById(ID id, boolean lock);

    List<EntityClass> findAll();

    EntityClass makePersistent(EntityClass entity);

    void makeTransient(EntityClass entity);
}