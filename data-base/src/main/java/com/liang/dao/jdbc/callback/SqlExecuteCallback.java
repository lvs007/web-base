/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.liang.dao.jdbc.callback;

import com.liang.dao.jdbc.common.Sql;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * @param <T>
 * @author
 */
public interface SqlExecuteCallback<T> {

    public T execute(Connection conn, Sql sql) throws SQLException;
}
