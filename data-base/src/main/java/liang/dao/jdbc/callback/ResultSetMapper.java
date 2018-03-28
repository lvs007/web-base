/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package liang.dao.jdbc.callback;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 从 resultset到对象之间的映射
 *
 * @author
 * @param <T>
 */
public interface ResultSetMapper<T> {

    public T mapper(ResultSet rs)throws SQLException;
}
