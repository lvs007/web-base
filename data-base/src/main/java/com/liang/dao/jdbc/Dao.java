/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.liang.dao.jdbc;

import com.liang.dao.jdbc.common.PageRequest;
import com.liang.dao.jdbc.common.PageResponse;
import com.liang.dao.jdbc.common.SearchFilter;
import com.liang.dao.jdbc.common.SqlPath;

import java.util.List;

/**
 * 基础的dao接口
 *
 * @param <T>
 * @param <ID>
 * @author
 */
public interface Dao<T, ID> {

    /**
     * 插入对象， 成功后会设置真实id
     *
     * @param t XXXEntity 实例
     * @return 成功返回true, 失败返回 false
     */
    boolean insert(T t);

    boolean insert(ContentValues cv);

    void insertBatch(List<T> list);

    boolean insertOrUpdate(T t);

    boolean update(T t);

    boolean update(ContentValues cv);

    boolean deleteById(ID id);

    boolean delete(T t);

    int deleteBatch(List<ID> idList);

    T findById(ID id);

    T findOne(SqlPath sqlPath);

    <S> S findOne(SqlPath sqlPath, Class<S> cls);

    List<T> findAll(SqlPath sqlPath);

    <S> List<S> findAll(SqlPath sqlPath, Class<S> cls);

    PageResponse<T> listAll(SqlPath sqlPath);

    /**
     * 太危险，准备移除
     *
     * @return
     */
    @Deprecated
    List<T> listAll();

    /**
     * 太危险，准备移除
     */
    @Deprecated
    void deleteAll();

    PageResponse<T> listAll(PageRequest request);

    List<T> listAllWithoutPageInfo(PageRequest request);

    PageResponse<T> listAll(List<SearchFilter> filters, PageRequest request);

    List<T> listAllWithoutPageInfo(List<SearchFilter> filters, PageRequest request);

    long count();

    long count(List<SearchFilter> filters);

    long count(SqlPath sqlPath);
}
