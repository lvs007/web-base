/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package liang.dao.jdbc.common;

import liang.dao.jdbc.EntitySequenceHandler;
import liang.dao.jdbc.callback.*;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 一个抽象的存储器，定义了一些常用的方法用于监控
 *
 * @author HadesLee
 */
public abstract class AbstractStorage {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractStorage.class);
    protected StorageManager storageManager;
    private JdbcTemplate jdbcTemplate;

    protected AbstractStorage() {
    }

    protected boolean execute(Sql sql) {
        return execute(sql, new SqlExecuteCallback<Boolean>() {
            @Override
            public Boolean execute(Connection conn, Sql sql) throws SQLException {
                PreparedStatement pre = sql.createPreparedStatement(conn);
                return pre.execute();
            }
        });
    }

    protected int[] executeBatch(Sql sql) {
        return execute(sql, new SqlExecuteCallback<int[]>() {
            @Override
            public int[] execute(Connection conn, Sql sql) throws SQLException {
                PreparedStatement pre = null;
                try {
                    pre = sql.createPreparedStatement(conn);
                    return pre.executeBatch();
                } finally {
                    close(pre);
                }
            }
        });
    }

    protected int executeUpdate(Sql sql) {
        return execute(sql, new SqlExecuteCallback<Integer>() {
            @Override
            public Integer execute(Connection conn, Sql sql) throws SQLException {
                PreparedStatement pre = null;
                try {
                    pre = sql.createPreparedStatement(conn);
                    return pre.executeUpdate();
                } finally {
                    close(pre);
                }

            }
        });
    }

    protected <T> List<T> executeQuery(Sql sql, final ResultSetMapper<T> mapper) {
        return executeQuery(sql, new SqlQueryCallback<List<T>>() {
            @Override
            public List<T> execute(ResultSet rs) throws SQLException {
                List<T> list = new ArrayList<>();
                while (rs.next()) {
                    list.add(mapper.mapper(rs));
                }
                return list;

            }
        });
    }

    protected final <T> T executeQuery(Sql sql, final SqlQueryCallback<T> callback) {
        return execute(sql, new SqlExecuteCallback<T>() {
            @Override
            public T execute(Connection conn, Sql sql) throws SQLException {
                PreparedStatement pre = null;
                ResultSet rs = null;
                try {
                    pre = sql.createPreparedStatement(conn);
                    rs = pre.executeQuery();
                    return callback.execute(rs);
                } finally {
                    close(rs);
                    close(pre);
                }
            }
        });
    }

    /**
     * 最类里面最基础的方法，所有其他的方法都是基于本方法进行扩展的
     * 本方法会监控数据库的调用，这样更有利于查看数据库的状态。
     * 当前监控是可以配置是否开启的，默认是开启的
     *
     * @param <T>
     * @param callback
     * @return
     */
    protected final <T> T execute(final ConnectionCallback<T> callback) {
        long from = System.currentTimeMillis();
        final StorageManager.SqlLine line = new StorageManager.SqlLine();
        boolean hasError = false;
        T t = null;
        try {
            final ConnectionCallback.ExecuteWatcher watcher = new ConnectionCallback.ExecuteWatcher() {
                @Override
                public void setSql(String sql) {
                    line.sql = sql;
                    LOG.debug(sql);
                }
            };
            boolean available = storageManager.isAvailable();
            if (available) {
                t = jdbcTemplate.execute(new org.springframework.jdbc.core.ConnectionCallback<T>() {
                    @Override
                    public T doInConnection(Connection con) throws SQLException, DataAccessException {
                        return callback.execute(con, watcher);
                    }
                });
            } else {
                LOG.warn("【{}】:当前数据库不可用，此次调用将被忽略.", storageManager.getName());
            }
        } catch (Throwable ex) {
            LOG.error(line.sql, ex);
            line.errorInfo = ex.getMessage();
            hasError = true;
            throw ex;
        } finally {
            line.usedTime = (System.currentTimeMillis() - from);
            if (hasError) {
                storageManager.addErrorCount(line);
            } else {
                storageManager.addExecuteCount(line);
            }
        }
        return t;

    }

    /**
     * 注意，这里的T是一个对象，由于此处可能会返回null,所以在接收基本类型的
     * 包装对象的时候，不要直接解包，而是用对象来接收返回值，再判断是否为null
     *
     * @param <T>
     * @param sql
     * @param callback
     * @return
     */
    protected final <T> T execute(final Sql sql, final SqlExecuteCallback<T> callback) {
        return execute(new ConnectionCallback<T>() {
            @Override
            public T execute(Connection conn, ExecuteWatcher watcher) throws SQLException {
                watcher.setSql(sql.toString());
                return callback.execute(conn, sql);
            }
        });
    }

    protected void close(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException ex) {
                LOG.warn(null, ex);
            }
        }
    }

    protected void close(Statement sta) {
        try {
            if (sta != null) {
                sta.close();
            }
        } catch (SQLException ex) {
            LOG.warn(null, ex);
        }
    }

    protected void close(ResultSet rs) {
        try {
            if (rs != null) {
                rs.close();
            }
        } catch (SQLException ex) {
            LOG.warn(null, ex);
        }
    }

    protected long countBySql(Sql sql) {
        Long c = executeQuery(sql, new SqlQueryCallback<Long>() {
            @Override
            public Long execute(ResultSet rs) throws SQLException {
                if (rs.next()) {
                    return rs.getLong(1);
                } else {
                    return 0L;
                }
            }
        });
        if (c != null) {
            return c;
        }
        return 0L;
    }

    protected <T> PageResponse<T> listBySql(Sql sql, PageRequest request, ResultSetMapper<T> mapper) {
        sql = sql.clone();
        Sql count = sql.count();
        return listBySql(sql, count, request, mapper);
    }

    protected <T> PageResponse<T> listBySql(Sql sql, PageRequest request, Class<T> cls) {
        return listBySql(sql, request, ResultSetMapperFactory.createBeanMapper(cls));
    }

    protected abstract String getColumnName(String fieldName);

    protected <T> PageResponse<T> listBySql(Sql sql, Sql countSql, PageRequest request, Class<T> cls) {
        return listBySql(sql, countSql, request, ResultSetMapperFactory.createBeanMapper(cls));
    }

    protected <T> PageResponse<T> listBySql(Sql sql, Sql countSql, PageRequest request, ResultSetMapper<T> mapper) {
        sql = sql.clone();
        List<PageRequest.Sort> sorts = request.getSort();
        if (CollectionUtils.isNotEmpty(sorts)) {
            StringBuilder sb = new StringBuilder();
            sb.append(" order by ");
            for (PageRequest.Sort sort : sorts) {
                sb.append(getColumnName(sort.getProperty())).append(" ").append(sort.getDirection()).append(",");
            }
            sb.deleteCharAt(sb.length() - 1);
            sql.setOrderBy(sb.toString());
        }
        sql.setPaging(request.getOffset(), request.getLimit());
        PageResponse<T> response = new PageResponse<>();
        response.setItemList(listBySql(sql, mapper));
        response.setTotal(countBySql(countSql));
        return response;
    }

    protected <T> List<T> listBySql(Sql sql, ResultSetMapper<T> mapper) {
        return executeQuery(sql, mapper);
    }

    protected <T> List<T> listBySql(Sql sql, Class<T> cls) {
        return executeQuery(sql, ResultSetMapperFactory.createBeanMapper(cls));
    }

    protected <T> void listBySql(final Sql sql, final EntitySequenceHandler<T> handler, final ResultSetMapper<T> mapper) {
        execute(new ConnectionCallback<Void>() {
            @Override
            public Void execute(Connection conn, ConnectionCallback.ExecuteWatcher watcher) throws SQLException {
                int offset = 0;
                int limit = 10000;
                watcher.setSql(sql.toString());
                System.out.println(sql);
                while (true) {
                    Sql exe = sql.clone();
                    exe.setPaging(offset, limit);
                    PreparedStatement pre = null;
                    ResultSet rs = null;
                    System.out.println(exe);
                    try {
                        pre = exe.createPreparedStatement(conn);
                        rs = pre.executeQuery();
                        if (rs.next()) {
                            do {
                                offset++;
                                handler.handleEntity(mapper.mapper(rs));
                            } while (rs.next());
                        } else {
                            return null;
                        }
                        if (offset % limit != 0) {
                            return null;
                        }
                    } finally {
                        close(rs);
                        close(pre);
                    }
                }
            }
        });
    }

    protected <T> void listById(final String sqlWithoutId, final Long fromId, final Long toId, final EntitySequenceHandler<T> handler, final ResultSetMapper<T> mapper) {
        execute(new ConnectionCallback<Void>() {
            @Override
            public Void execute(Connection conn, ConnectionCallback.ExecuteWatcher watcher) throws SQLException {
                int offset = 0;
                int limit = 10000;
                watcher.setSql(sqlWithoutId);
                System.out.println(sqlWithoutId);
                long idFrom = fromId == null ? 0L : fromId;
                long idTo = toId == null ? Long.MAX_VALUE : toId;
                long currentId = idFrom;
                while (true) {
                    Sql exe = new Sql(sqlWithoutId + " where id >=? and id <=? limit " + limit);
                    exe.addParam(currentId).addParam(idTo);
                    PreparedStatement pre = null;
                    ResultSet rs = null;
                    System.out.println("read:" + offset + ",sql=" + exe);
                    try {
                        pre = exe.createPreparedStatement(conn);
                        rs = pre.executeQuery();
                        if (rs.next()) {
                            do {
                                offset++;
                                currentId = rs.getLong("id");
                                handler.handleEntity(mapper.mapper(rs));
                            } while (rs.next());
                        } else {
                            return null;
                        }
                        if (offset % limit != 0) {
                            return null;
                        }
                    } finally {
                        close(rs);
                        close(pre);
                    }
                }
            }
        });
    }

    /**
     * 返回当前数据源相关的连接，使用完以后，必须手动关闭
     *
     * @return
     * @throws SQLException
     */
    protected Connection getConnection() throws SQLException {
        Connection conn = jdbcTemplate.getDataSource().getConnection();
        LOG.debug("获取数据库连接:" + conn.getMetaData().getURL());
        return conn;
    }

    @Autowired
    public void setStorageManager(StorageManager storageManager) {
        this.storageManager = storageManager;
    }

    @Autowired
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
}
