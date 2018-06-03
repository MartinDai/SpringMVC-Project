package com.doodl6.springmvc.dao.api;

public interface BaseMapper<T> {

    void insert(T entity);

    T getById(long id);

}
