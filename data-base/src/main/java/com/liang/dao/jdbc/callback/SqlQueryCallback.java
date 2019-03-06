/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.liang.dao.jdbc.callback;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author
 * @param <T>
 */
public interface SqlQueryCallback<T> {
    /**
     * 这里的resultset是刚刚查到的，还没有做任何操作的
     * 这里只需要遍历，不用管任何释放的问题
     * @param rs
     * @return 
     * @throws SQLException
     */
    public T execute(ResultSet rs)throws SQLException;
}
