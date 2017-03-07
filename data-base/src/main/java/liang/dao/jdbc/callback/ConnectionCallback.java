/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package liang.dao.jdbc.callback;

import java.sql.Connection;
import java.sql.SQLException;

/**
 *
 * @author
 * @param <T>
 */
public interface ConnectionCallback<T> {

    public T execute(Connection conn, ExecuteWatcher watcher) throws SQLException;

    public static interface ExecuteWatcher {

        public void setSql(String sql);
    }
}
