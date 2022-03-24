package com.mws.backend.framework.database;

public interface GenericDao<T, Id> {


    T create(T t);

    void delete(Id id);

    T find(Id id);

    T update(T t);
}