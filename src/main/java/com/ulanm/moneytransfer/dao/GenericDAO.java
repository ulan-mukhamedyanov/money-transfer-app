package com.ulanm.moneytransfer.dao;

import com.ulanm.moneytransfer.model.GenericModel;

import java.util.List;

public interface GenericDAO<T extends GenericModel> {

    List<T> getAll();

    T getById(String id);

    boolean submit(T entity);

    boolean delete(T entity);

    T create();

}
