/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.liang.dao.jdbc.common;

import com.alibaba.fastjson.JSON;
import com.liang.common.exception.Exceptions;
import com.liang.common.exception.NotSupportException;
import com.liang.dao.jdbc.callback.ConnectionCallback;
import com.liang.dao.jdbc.callback.ResultSetMapper;
import com.liang.dao.jdbc.callback.ResultSetMapperFactory;
import com.liang.dao.jdbc.callback.SqlExecuteCallback;
import com.liang.dao.jdbc.callback.SqlQueryCallback;
import com.liang.dao.jdbc.split.listener.FailListener;
import com.liang.dao.jdbc.EntitySequenceHandler;
import com.liang.dao.jdbc.split.ParseSql;
import com.liang.dao.jdbc.split.common.LogUtil;
import com.liang.dao.jdbc.split.common.SqlCommonUtil;
import com.liang.dao.jdbc.split.db.DBIndexHelper;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.map.HashedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.*;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * 一个抽象的存储器，定义了一些常用的方法用于监控
 *
 * @author
 */
public abstract class AbstractStorage extends ParseSql {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractStorage.class);
    protected StorageManager storageManager;
    private JdbcTemplate jdbcTemplate;

    protected AbstractStorage() {

    }

    protected boolean execute(Sql sql) {
        return execute(sql, new SqlExecuteCallback<Boolean>() {
            @Override
            public Boolean execute(Connection conn, Sql sql) throws SQLException {
                try (PreparedStatement pre = sql.createPreparedStatement(conn)) {
                    return pre.execute();
                }
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
    protected final <T> T execute(final ConnectionCallback<T> callback, Sql sql) {
        long from = System.currentTimeMillis();
        final StorageManager.SqlLine line = new StorageManager.SqlLine();
        boolean hasError = false;
        T t = null;
        try {
            //在这里设置数据源
            setDataSourceLookUp(sql);
            //
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
            throw Exceptions.unchecked(ex);
        } finally {
            //清除数据源绑定
            DBIndexHelper.clean();
            //
            line.usedTime = (System.currentTimeMillis() - from);
            if (hasError) {
                FailListener failListener = selectListener(sql);
                if (failListener != null) {
                    failListener.doSomething(sql);
                }
                storageManager.addErrorCount(line);
            } else {
                storageManager.addExecuteCount(line);
            }
        }
        return t;

    }

    @Override
    public long executeQueryForSplitCount(Sql sql) {
        return executeQuery(sql, new SqlQueryCallback<Long>() {
            @Override
            public Long execute(ResultSet rs) throws SQLException {
                return rs.getLong("count");
            }
        });
    }

    public List<Map<String, Object>> executeQueryForSplit(Sql sql) {
        return executeQueryForSplit(sql, new ResultSetMapper<Map<String, Object>>() {
            @Override
            public Map<String, Object> mapper(ResultSet rs) throws SQLException {
                Map<String, Object> map = new HashedMap();
                ResultSetMetaData metaData = rs.getMetaData();
                for (int i = 1; i <= metaData.getColumnCount(); i++) {
                    String column = metaData.getColumnName(i);
                    Object value = rs.getObject(column);
                    map.put(column, value);
                }
                return map;
            }
        });
    }

    protected List<Map<String, Object>> executeQueryForSplit(Sql sql, final ResultSetMapper<Map<String, Object>> mapper) {
        return executeQuery(sql, new SqlQueryCallback<List<Map<String, Object>>>() {
            @Override
            public List<Map<String, Object>> execute(ResultSet rs) throws SQLException {
                List<Map<String, Object>> list = new ArrayList<>();
                while (rs.next()) {
                    list.add(mapper.mapper(rs));
                }
                return list;

            }
        });
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
        T t;
        long begin = System.currentTimeMillis();
        if (isSplitTable() && !sql.isBypass()) {
            List<Sql> sqlList = parseSql(sql);
            LogUtil.info("分表后的数据表：{},{}", JSON.toJSONString(sqlList), JSON.toJSONString(sql));
            if (CollectionUtils.isEmpty(sqlList)) {
                t = executeInner(sql, callback);
            } else {
                try {
                    t = merge(sqlList, callback);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        } else {
            t = executeInner(sql, callback);
        }
        long end = System.currentTimeMillis();
        LogUtil.info("execute sql spend time:{} ms", (end - begin));
        return t;
    }

    private <T> T merge(List<Sql> sqlList, final SqlExecuteCallback<T> callback) throws ExecutionException, InterruptedException {
        Collection collection = new ArrayList();
        Map map = new HashMap();
        Integer integer = 0;
        Long l = 0L;
        Double d = 0.0;
        Float f = 0F;
        Object obj = null;
        List<Future<T>> futureList = new ArrayList<>();
        for (final Sql sqlTemp : sqlList) {
            Future<T> future = splitTableExecutor.submit(new Callable<T>() {
                @Override
                public T call() throws Exception {
                    try {
                        return executeInner(sqlTemp, callback);
                    } catch (Throwable e) {
                        if (isSelect(sqlTemp)) {
                            throw e;
                        } else {
                            LOG.error("AbstractStorage.merge insert or update or delete error", e);
                        }
                    }
                    return null;
                }
            });
            futureList.add(future);
        }
        for (Future<T> future : futureList) {
            T tmp = future.get();
            if (tmp instanceof Collection) {
                collection.addAll((Collection) tmp);
                obj = collection;
            } else if (tmp instanceof Integer) {
                integer += (Integer) tmp;
                obj = integer;
            } else if (tmp instanceof Long) {
                l += (Long) tmp;
                obj = l;
            } else if (tmp instanceof Double) {
                d += (Double) tmp;
                obj = d;
            } else if (tmp instanceof Float) {
                f += (Float) tmp;
                obj = f;
            } else if (tmp instanceof Short || tmp instanceof Byte) {
                throw NotSupportException.throwException("不支持返回类型是short,byte");
            } else if (tmp instanceof Map) {
                map.putAll((Map) tmp);
                obj = map;
            } else {
                if (tmp != null) {
                    obj = tmp;
                }
            }
        }
        return (T) obj;
    }

    private <T> T executeInner(final Sql sql, final SqlExecuteCallback<T> callback) {
        return execute(new ConnectionCallback<T>() {
            @Override
            public T execute(Connection conn, ExecuteWatcher watcher) throws SQLException {
                watcher.setSql(sql.toString());
                return callback.execute(conn, sql);
            }
        }, sql);
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
        if (isSplitTable(SqlCommonUtil.getTable(sql))) {
            throw NotSupportException.throwException("当前表是拆表，不支持此操作！");
        }
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
        }, sql);
    }

    protected <T> void listById(final String sqlWithoutId, final Long fromId, final Long toId, final EntitySequenceHandler<T> handler, final ResultSetMapper<T> mapper) {
        Sql sql = new Sql(sqlWithoutId);
        if (isSplitTable(SqlCommonUtil.getTable(sql))) {
            throw NotSupportException.throwException("当前表是拆表，不支持此操作！");
        }
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
        }, sql);
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
