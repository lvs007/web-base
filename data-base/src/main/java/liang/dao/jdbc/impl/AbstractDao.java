/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package liang.dao.jdbc.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import liang.common.util.MiscUtils;
import liang.dao.jdbc.callback.SqlExecuteCallback;
import liang.dao.jdbc.callback.SqlQueryCallback;
import liang.dao.jdbc.common.*;
import liang.dao.jdbc.BaseDao;
import liang.dao.jdbc.ContentValues;
import liang.dao.jdbc.EntitySequenceHandler;
import liang.dao.jdbc.callback.ResultSetMapper;
import liang.dao.jdbc.callback.ResultSetMapperFactory;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import java.lang.reflect.ParameterizedType;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 所有数据库操作类的父类
 *
 * @param <T>
 * @author
 */
public class AbstractDao<T> extends AbstractStorage implements BaseDao<T> {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractDao.class);
    private CrudBuilder<T> builder;
    private final CrudBuilder<EntityLogEntity> fuckBuilder = new CrudBuilder<>(EntityLogEntity.class);
    private static final boolean DEBUG = false;
    private static volatile boolean initTable;//是否初始化过了table

    public AbstractDao() {
    }

    private void doAudit(EntityLogEntity entity, boolean success) {
        if (entity != null && DEBUG == false) {
            entity.setSuccess(success);
            insertByCrudBuilder(fuckBuilder, entity);
        }
    }

    /**
     * 此方法必须在相关实体数据库操作之前调用
     * 暂时不支持此方法
     *
     * @param operation
     * @return
     */
    private EntityLogEntity buildEntityLog(String operation, ContentValues cv) {
        return new EntityLogEntity();
    }

    /**
     * 此方法必须在相关实体数据库操作之前调用
     * 暂时不支持此方法
     *
     * @param operation
     * @param t
     * @return
     */
    private EntityLogEntity buildEntityLog(String operation, T t) {
        return new EntityLogEntity();
    }

    private Map<String, Object> getAuditContentMap(T t) {
        return MiscUtils.toMapExclude(t, getBuilder().getNoAuditFields());
    }

    private String getAuditContent(T t) {
        Map<String, Object> map = MiscUtils.toMapExclude(t, getBuilder().getNoAuditFields());
        return toAuditJson(map);
    }

    private String toAuditJson(Object o) {
        return JSON.toJSONString(o, SerializerFeature.BrowserCompatible, SerializerFeature.WriteMapNullValue);
    }

    /**
     * 此方法必须在相关实体数据库操作之前调用
     *
     * @return
     */
    private EntityLogEntity buildEntityLogByDeleteId(Long id) {
        return new EntityLogEntity();
    }

    /**
     * 此方法必须在相关实体数据库操作之前调用
     *
     * @return
     */
    private EntityLogEntity buildEntityLogByName(String name) {
        return new EntityLogEntity();
    }

    @Override
    public boolean update(T t) {
        boolean success = false;
        EntityLogEntity logEntity = buildEntityLog("update", t);
        try {
            Sql sql = getBuilder().getUpdateSql();
            List<Object> params = getBuilder().buildUpdateParams(t);
            sql.addAllParams(params);
            if (DEBUG) {
                System.out.println(sql);
                return true;
            }
            success = executeUpdate(sql) > 0;
            return success;
        } finally {
            doAudit(logEntity, success);
        }
    }

    @Override
    public boolean update(ContentValues cv) {
        boolean success = false;
        EntityLogEntity logEntity = buildEntityLog("update", cv);
        try {
            if (cv.size() <= 1) {
                LOG.warn("更新的时侯没有条件，cv={}", cv);
                return false;
            }
            Sql sql = getBuilder().buildUpdateSql(cv);
            if (DEBUG) {
                System.out.println(sql);
                return true;
            }
            success = executeUpdate(sql) > 0;
            return success;
        } finally {
            doAudit(logEntity, success);
        }

    }

    @Override
    public boolean deleteById(Long id) {
        boolean success = false;
        EntityLogEntity entity = buildEntityLogByDeleteId(id);
        try {
            Sql sql = getBuilder().getDeleteSql();
            sql.addParam(id);
            if (DEBUG) {
                System.out.println(sql);
                return true;
            }
            int i = executeUpdate(sql);
            success = i > 0;
            return success;
        } finally {
            doAudit(entity, success);
        }
    }

    @Override
    public int deleteBatch(List<Long> idList) {
        EntityLogEntity entity = buildEntityLogByName("deleteBatch");
        int back = -1;
        boolean success = false;
        try {
            List<Object> params = new ArrayList<>();
            StringBuilder sb = new StringBuilder();
            sb.append("delete from ").append(getTableName()).append(" where ").append(getBuilder().getIdName()).append(" in (");
            for (int i = 0; i < idList.size(); i++) {
                if (i > 0) {
                    sb.append(",");
                }
                sb.append("?");
                params.add(idList.get(i));
            }
            sb.append(")");
            Sql sql = new Sql(sb.toString());
            sql.addAllParams(params);
            back = executeUpdate(sql);
            success = back > 0;
        } finally {
            doAudit(entity, success);
        }
        return back;
    }

    @Override
    public boolean delete(T t) {
        try {
            Long id = (Long) getBuilder().getIdField().get(t);
            return deleteById(id);
        } catch (IllegalArgumentException | IllegalAccessException ex) {
            LOG.error(null, ex);
        }
        return false;
    }

    @Override
    public void deleteAll() {
        boolean success = false;
        EntityLogEntity entity = buildEntityLogByName("deleteAll");
        try {
            Sql sql = new Sql("delete from " + getTableName());
            success = executeUpdate(sql) > 0;
        } finally {
            doAudit(entity, success);
        }
    }

    @Override
    public void insertBatch(List<T> list) {
        if (CollectionUtils.isNotEmpty(list)) {
            int maxBatchSize = storageManager.getInsertBatchMaxSize();
            if (list.size() < maxBatchSize) {
                insertBatch0(list);
            } else {
                int from = 0;
                do {
                    List<T> subList = list.subList(from, from + maxBatchSize);
                    insertBatch0(subList);
                    from += maxBatchSize;
                } while (from + maxBatchSize <= list.size());
                if (from < list.size()) {
                    List<T> subList = list.subList(from, list.size());
                    insertBatch0(subList);
                }
            }
        }
    }

    private void insertBatch0(List<T> list) {
        Sql sql = getBuilder().getInsertSql();
        for (T t : list) {
            sql.addAllParams(getBuilder().buildInsertParams(t));
            sql.addBatch();
        }
        executeBatch(sql);
    }

    private <Fuck> boolean insertByCrudBuilder(CrudBuilder<Fuck> crud, Fuck fuck) {
        boolean success = false;
        Sql sql = crud.getInsertSql();
        sql.addAllParams(crud.buildInsertParams(fuck));
        if (DEBUG) {
            System.out.println(sql);
            success = true;
        }
        Long id = execute(sql, new SqlExecuteCallback<Long>() {
            @Override
            public Long execute(Connection conn, Sql sql) throws SQLException {
                PreparedStatement pre = null;
                ResultSet rs = null;
                try {
                    pre = sql.createPreparedStatement(conn, PreparedStatement.RETURN_GENERATED_KEYS);
                    pre.executeUpdate();
                    rs = pre.getGeneratedKeys();
                    if (rs.next()) {
                        return rs.getLong(1);
                    }
                } finally {
                    close(rs);
                    close(pre);
                }
                return null;
            }
        });
        if (id != null) {
            try {
                crud.getIdField().set(fuck, id);
                success = true;
            } catch (IllegalArgumentException | IllegalAccessException ex) {
                LOG.warn(null, ex);
            }
        }
        return success;
    }

    @Override
    public boolean insert(T t) {
        boolean success = false;
        try {
            success = insertByCrudBuilder(builder, t);
            return success;
        } finally {
        }
    }

    @Override
    public boolean insert(ContentValues cv) {
        boolean success = false;
        EntityLogEntity entity = buildEntityLog("insert", cv);
        try {
            Sql sql = getBuilder().buildInsertSql(cv);
            if (DEBUG) {
                System.out.println(sql);
                return true;
            }
            success = executeUpdate(sql) > 0;
            return success;
        } finally {
            if (entity != null) {
                Object idValue = cv.getValueByFieldName(getBuilder().getIdName());
                if (idValue != null && idValue instanceof Long) {
                    entity.setEntityId((Long) idValue);
                }
            }
            doAudit(entity, success);
        }

    }

    @Override
    public boolean insertOrUpdate(T t) {
        try {
            Long id = (Long) getBuilder().getIdField().get(t);
            if (id == null || id <= 0) {
                return insert(t);
            } else {
                return update(t);
            }
        } catch (IllegalArgumentException | IllegalAccessException ex) {
            LOG.error(null, ex);
        }
        return false;
    }

    protected String getTableName() {
        return getBuilder().getTableName();
    }

    @Override
    public List<T> listAll() {
        Sql sql = getBuilder().getSelectAllSql();
        if (DEBUG) {
            System.out.println(sql);
            return null;
        }
        return listBySql(sql);
    }

    @Override
    public PageResponse<T> listAll(PageRequest request) {
        PageResponse<T> page = new PageResponse<>();
        page.setTotal(count());
        page.setItemList(listAllWithoutPageInfo(request));
        return page;
    }

    @Override
    public List<T> listAllWithoutPageInfo(PageRequest request) {
        StringBuilder sb = new StringBuilder();
        sb.append("select * from ").append(getTableName());
        List<PageRequest.Sort> sorts = request.getSort();
        if (CollectionUtils.isNotEmpty(sorts)) {
            sb.append(" order by ");
            for (PageRequest.Sort sort : sorts) {
                sb.append(getBuilder().getColumnName(sort.getProperty())).append(" ").append(sort.getDirection()).append(",");
            }
            sb.deleteCharAt(sb.length() - 1);
        }
        sb.append(" limit ").append(request.getOffset());
        sb.append(",").append(request.getLimit());
        Sql sql = new Sql(sb.toString());
        return listBySql(sql);
    }

    @Override
    public List<T> findAll(SqlPath sqlPath) {
        return listBySql(getBuilder().buildSelect(sqlPath));
    }

    @Override
    public <S> List<S> findAll(SqlPath sqlPath, Class<S> cls) {
        return listBySql(getBuilder().buildSelect(sqlPath), cls);
    }

    @Override
    public T findOne(SqlPath sqlPath) {
        sqlPath.limit(1);
        List<T> list = listBySql(getBuilder().buildSelect(sqlPath));
        if (CollectionUtils.isNotEmpty(list)) {
            return list.get(0);
        }
        return null;
    }

    @Override
    public <S> S findOne(SqlPath sqlPath, Class<S> cls) {
        sqlPath.limit(1);
        return findBySql(getBuilder().buildSelect(sqlPath), cls);
    }

    @Override
    public PageResponse<T> listAll(List<SearchFilter> filters, PageRequest request) {
        PageResponse<T> page = new PageResponse<>();
        page.setTotal(count(filters));
        page.setItemList(listAllWithoutPageInfo(filters, request));
        return page;
    }

    @Override
    public PageResponse<T> listAll(SqlPath sqlPath) {
        PageResponse<T> page = new PageResponse<>();
        page.setTotal(count(sqlPath));
        page.setItemList(findAll(sqlPath));
        return page;
    }

    @Override
    public List<T> listAllWithoutPageInfo(List<SearchFilter> filters, PageRequest request) {
        Sql selectSql = getBuilder().buildSelect(filters, request);
        return listBySql(selectSql);
    }

    @Override
    public long count(List<SearchFilter> filters) {
        Sql countSql = getBuilder().buildCount(filters);
        return countBySql(countSql);
    }

    @Override
    public long count() {
        Sql sql = new Sql("select count(*) from " + getTableName());
        return countBySql(sql);
    }

    @Override
    public long count(SqlPath sqlPath) {
        return countBySql(getBuilder().buildCount(sqlPath));
    }

    protected PageResponse<T> listBySql(Sql sql, PageRequest request) {
        sql = sql.clone();
        Sql count = sql.count();
        return listBySql(sql, count, request);
    }

    protected PageResponse<T> listBySql(Sql sql, Sql countSql, PageRequest request) {
        return listBySql(sql, countSql, request, new ResultSetMapper<T>() {

            @Override
            public T mapper(ResultSet rs) throws SQLException {
                return from(rs);
            }
        });
    }

    protected List<T> listBySql(Sql sql) {
        return listBySql(sql, new ResultSetMapper<T>() {

            @Override
            public T mapper(ResultSet rs) throws SQLException {
                return from(rs);
            }
        });
    }

    protected void listBySql(final Sql sql, final EntitySequenceHandler<T> handler) {
        listBySql(sql, handler, new ResultSetMapper<T>() {

            @Override
            public T mapper(ResultSet rs) throws SQLException {
                return from(rs);
            }
        });
    }

    protected void listById(final String sql, Long idFrom, Long idTo, final EntitySequenceHandler<T> handler) {
        listById(sql, idFrom, idTo, handler, new ResultSetMapper<T>() {

            @Override
            public T mapper(ResultSet rs) throws SQLException {
                return from(rs);
            }
        });
    }

    @Override
    public T findById(Long id) {
        Sql sql = getBuilder().getSelectByIdSql();
        sql.addParam(id);
        if (DEBUG) {
            System.out.println(sql);
            return null;
        }
        return findBySql(sql);
    }

    protected final T findBySql(Sql sql) {
        return executeQuery(sql, new SqlQueryCallback<T>() {
            @Override
            public T execute(ResultSet rs) throws SQLException {
                if (rs.next()) {
                    return from(rs);
                }
                return null;
            }
        });
    }

    protected <S> S findBySql(Sql sql, Class<S> cls) {
        return findBySql(sql, ResultSetMapperFactory.createBeanMapper(cls));
    }

    protected <S> List<S> listBySql(Sql sql, Class<S> cls) {
        return listBySql(sql, ResultSetMapperFactory.createBeanMapper(cls));
    }

    protected <S> S findBySql(Sql sql, final ResultSetMapper<S> mapper) {
        return executeQuery(sql, new SqlQueryCallback<S>() {
            @Override
            public S execute(ResultSet rs) throws SQLException {
                if (rs.next()) {
                    return mapper.mapper(rs);
                }
                return null;
            }
        });
    }

    protected CrudBuilder<T> getBuilder() {
        if (builder == null) {
            builder = new CrudBuilder<>(getEntityClass());
        }
        return builder;
    }

    @SuppressWarnings("unchecked")
    protected Class<T> getEntityClass() {
        ParameterizedType type = (ParameterizedType) this.getClass().getGenericSuperclass();
        return (Class<T>) type.getActualTypeArguments()[0];
    }

    @Override
    protected String getColumnName(String fieldName) {
        return getBuilder().getColumnName(fieldName);
    }

    /**
     * 从结果集里面生成一个实体类的对象，默认是从反射组装，这样就要求
     * 实体类必须有一个无参的构造函数
     * 子类可以有自己的组装方式。
     *
     * @param rs
     * @return
     * @throws SQLException
     */
    protected T from(ResultSet rs) throws SQLException {
        return getBuilder().from(rs);
    }

    private void createEntityLogTable() throws Exception {
        Connection conn = getConnection();
        if (conn == null) {
            throw new IllegalArgumentException("获取到的数据库连接为null");
        }
        try {
//            TableUpdater.tryUpdate("t_entity_log", conn);
        } finally {
            conn.close();
        }
    }

    @PostConstruct
    void initInnerTable() {
        if (initTable == false) {
            initTable = true;
            try {
                createEntityLogTable();
            } catch (Exception ex) {
                LOG.error(null, ex);
            }
        }
    }
}
