/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.liang.dao.jdbc.impl;

/**
 *
 * @author
 * @param <T>
 */
public class CommonDao<T> extends AbstractDao<T> {

    private final Class<T> entityClass;

    public CommonDao(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    @Override
    protected Class<T> getEntityClass() {
        return entityClass;
    }
}
