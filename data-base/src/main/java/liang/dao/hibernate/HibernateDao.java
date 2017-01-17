package liang.dao.hibernate;

import liang.dao.common.Dao;
import liang.dao.common.Page;
import liang.dao.common.SqlStatement;
import org.hibernate.*;
import org.hibernate.criterion.Restrictions;
import org.hibernate.jdbc.Work;
import org.hibernate.metadata.ClassMetadata;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.io.InputStream;
import java.io.Reader;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.sql.*;
import java.util.*;
import java.util.Date;
import java.util.Map.Entry;

/**
 * 基于Hibernate的数据访问<br/>
 * 对于给定的SQL语句,参数值可以是任意类型<br/>
 * 针对不同对象,处理情况不同,具体如下:
 * <ul>
 * <li>简单类型,包括基本类型以及String,Date,Number...,根据其索引位置为JPA参数 ?n 赋值</li>
 * <li>Map对象,根据key值将对应的value赋值给同名参数</li>
 * <li>POJO对象,根据属性名称进行同名参数赋值</li>
 * 对于简单类型的定义由实现类决定,以上所有类型均支持in操作
 * </ul>
 * 示例:
 * <pre>
 * <code>Map<String, Object> map = new HashMap<String, Object>();
 * map.put("idList", new Long[]{1L, 2L, 3L});
 * map.put("title", "%测试2%");
 * EventAlias event = new EventAlias();
 * event.setTitle("%测试%");
 * sql = "select e1.event_id ID,e1.event_date Date,e1.title,e1.type from event e1 where e1.title like :title or e1.title like ?1 and e1.event_id in :idList";
 * list = hibernateDao.sqlQuery(EventAlias.class, sql,event, "%中国%", map);</code>
 * </pre>
 *
 * @author skyfalling
 */
@Repository("hibernateDao")
@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
public class HibernateDao implements Dao {

    @Resource(name = "sessionFactory")
    protected SessionFactory sessionFactory;

    public HibernateDao() {

    }

    /**
     * 构造方法,指定sessionFactory
     *
     * @param sessionFactory
     */
    public HibernateDao(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    /**
     * 设置sessionFactory对象
     *
     * @param sessionFactory
     */
    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    /**
     * 获取当前session
     *
     * @return
     */
    protected Session getSession() {
        return sessionFactory.getCurrentSession();
    }


    @Override
    public <T> T get(T entity) {
        Class entityClass = getEntityClass(entity);
        Serializable id = getClassMetadata(entityClass).getIdentifier(entity, null);
        return (T) get(entityClass, id);
    }

    @Override
    public <T, K extends Serializable> T get(Class<T> entityClass, K id) {
        entityClass = getEntityClass(entityClass);
        return (T) getSession().get(entityClass, id);
    }

    @Override
    public <T> List<T> getAll(Class<T> entityClass) {
        entityClass = getEntityClass(entityClass);
        return createCriteria(entityClass).list();
    }

    @Override
    public <T, K extends Serializable> List<T> getByIds(Class<T> entityClass, List<K> ids) {
        entityClass = getEntityClass(entityClass);
        String idName = getClassMetadata(entityClass).getIdentifierPropertyName();
        return createCriteria(entityClass).add(Restrictions.in(idName, ids)).list();
    }

    @Override
    public <T> List<T> getByProperties(Class<T> entityClass, Map<String, Object> properties) {
        entityClass = getEntityClass(entityClass);
        return createCriteria(entityClass).add(Restrictions.allEq(properties)).list();
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public void delete(Object entity) {
        getSession().delete(entity);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public <K extends Serializable> void delete(Class entityClass, K id) {
        delete(getClassMetadata(entityClass).instantiate(id, null));
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public <T> T save(T entity) {
        return (T) getSession().merge(entity);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public <T> void save(List<T> entities) {
        for (T entity : entities) {
            save(entity);
        }
    }

    /**
     * @see #createSQLQuery(String, Object...)
     */
    @Override
    public List<Map<String, ?>> sqlQuery(String sql, Object... parameters) {
        return createSQLQuery(sql, parameters).setResultTransformer(new MapResultTransFormer()).list();
    }


    /**
     * @see #createSQLQuery(String, Object...)
     */
    @Override
    public <T> List<T> sqlQuery(Class<T> beanClass, String sql, Object... parameters) {
        return setQueryType(createSQLQuery(sql, parameters), beanClass).list();
    }


    /**
     * @see #createSQLQuery(String, Object...)
     */
    @Override
    public List<Map<String, ?>> sqlPageQuery(String sql, int pageNo, int pageSize, Object... parameters) {
        Query query = createSQLQuery(sql, parameters).setResultTransformer(new MapResultTransFormer());
        return pageQuery(query, pageNo, pageSize);
    }

    /**
     * @see #createSQLQuery(String, Object...)
     */
    @Override
    public <T> List<T> sqlPageQuery(Class<T> beanClass, String sql, int pageNo, int pageSize, Object... parameters) {
        return pageQuery(setQueryType(createSQLQuery(sql, parameters), beanClass), pageNo, pageSize);
    }


    /**
     * @see #createSQLQuery(String, Object...)
     */
    @Override
    public Page<Map<String, ?>> sqlPageQuery(Page page, String sql, Object... parameters) {
        page.setResult(sqlPageQuery(sql, page.getPageNo(), page.getPageSize(), parameters));
        return page;
    }


    /**
     * @see #createSQLQuery(String, Object...)
     */
    @Override
    public <T> Page<T> sqlPageQuery(Page<T> page, Class<T> beanClass, String sql, Object... parameters) {
        page.setResult(sqlPageQuery(beanClass, sql, page.getPageNo(), page.getPageSize(), parameters));
        return page;
    }


    @Override
    public void executeBatch(final String[] sqlList) {
        getSession().doWork(new Work() {
            @Override
            public void execute(Connection connection) throws SQLException {
                Statement stmt = connection.createStatement();
                for (String sql : sqlList) {
                    stmt.addBatch(sql);
                }
                stmt.executeBatch();
            }
        });
    }

    @Override
    public void executeBatch(final String sql, final Object[]... parameters) {
        getSession().doWork(new Work() {
            @Override
            public void execute(Connection connection) throws SQLException {
                PreparedStatement stmt = connection.prepareStatement(sql);
                for (Object[] arr : parameters) {
                    int i = 1;
                    for (Object p : arr) {
                        stmt.setObject(i++, p);
                    }
                    stmt.addBatch();
                }
                stmt.executeBatch();
            }
        });
    }

    /**
     * 执行HQL查询<br/>
     *
     * @param hql
     * @param parameters
     * @return
     * @see #createQuery(String, Object...)
     */
    public List hqlQuery(String hql, Object... parameters) {
        return createQuery(hql, parameters).list();
    }


    /**
     * 执行HQL分页查询<br/>
     *
     * @param hql
     * @param pageNo
     * @param pageSize
     * @param parameters
     * @return
     * @see #createQuery(String, Object...)
     */
    public List hqlPageQuery(String hql, int pageNo, int pageSize, Object... parameters) {
        return pageQuery(createQuery(hql, parameters), pageNo, pageSize);
    }

    /**
     * @see #createQuery(String, Object...)
     */
    public Page hqlPageQuery(Page page, String hql, Object... parameters) {
        page.setResult(hqlPageQuery(hql, page.getPageNo(), page.getPageSize(), parameters));
        return page;
    }


    /**
     * 分页查询
     *
     * @param query
     * @param pageNo   页码
     * @param pageSize 每页数据大小
     * @return
     */
    public static List pageQuery(Query query, int pageNo, int pageSize) {
        return query.setFirstResult((pageNo - 1) * pageSize).setMaxResults(pageSize).list();
    }

    /**
     * 分页查询
     *
     * @param query
     * @return
     */
    public static Page pageQuery(Query query, Page page) {
        page.setResult(pageQuery(query, page.getPageNo(), page.getPageSize()));
        return page;
    }


    /**
     * 创建SQLQuery对象,并设置参数<br/>
     * 参数值可以是任意类型<br/>
     * 针对不同对象,处理情况不同,具体如下:
     * <ul>
     * <li>简单类型,包括基本类型以及String,Date,Number...,根据其索引位置为JPA参数 ?n 赋值</li>
     * <li>Map对象,根据key值将对应的value赋值给同名参数</li>
     * <li>POJO对象,根据属性名称进行同名参数赋值</li>
     * 对于简单类型的定义由实现类决定,以上所有类型均支持in操作
     * </ul>
     * 示例:
     * <pre>
     * <code>Map<String, Object> map = new HashMap<String, Object>();
     * map.put("idList", new Long[]{1L, 2L, 3L});
     * map.put("title", "%测试2%");
     * EventAlias event = new EventAlias();
     * event.setTitle("%测试%");
     * sql = "select e1.event_id ID,e1.event_date Date,e1.title,e1.type from event e1 where e1.title like :title or e1.title like ?1 and e1.event_id in :idList";
     * list = hibernateDao.sqlQuery(EventAlias.class, sql,event, "%中国%", map);</code>
     * </pre>
     *
     * @param sql
     * @param parameters
     * @return
     */
    public SQLQuery createSQLQuery(String sql, Object... parameters) {
        return setParameters(getSession().createSQLQuery(sql), parameters);
    }


    /**
     * 创建SQLQuery对象,并设置参数<br/>
     *
     * @param sqlStatement
     * @return
     */
    public SQLQuery createSQLQuery(SqlStatement sqlStatement) {
        SQLQuery query = createSQLQuery(sqlStatement.preparedSql());
        int i = 0;
        for (Object value : sqlStatement.preparedParameters()) {
            query.setParameter(i++, value);
        }
        return query;
    }

    /**
     * 创建Query对象,并设置参数<br/>
     * 参数值可以是任意类型<br/>
     * 针对不同对象,处理情况不同,具体如下:
     * <ul>
     * <li>简单类型,包括基本类型以及String,Date,Number...,根据其索引位置为JPA参数 ?n 赋值</li>
     * <li>Map对象,根据key值将对应的value赋值给同名参数</li>
     * <li>POJO对象,根据属性名称进行同名参数赋值</li>
     * 对于简单类型的定义由实现类决定,以上所有类型均支持in操作<br/>
     * 此外,如果SQL(HQL)语句中只包含JDBC占位符,支持按索引位置赋值,但不支持in操作
     * </ul>
     * 示例:
     * <pre>
     * <code>Map<String, Object> map = new HashMap<String, Object>();
     * map.put("idList", new Long[]{1L, 2L, 3L});
     * map.put("title", "%测试2%");
     * EventAlias event = new EventAlias();
     * event.setTitle("%测试%");
     * sql = "select e1.event_id ID,e1.event_date Date,e1.title,e1.type from event e1 where e1.title like :title or e1.title like ?1 and e1.event_id in :idList";
     * list = hibernateDao.sqlQuery(EventAlias.class, sql,event, "%中国%", map);</code>
     * </pre>
     *
     * @param hql
     * @param parameters
     * @return
     */
    public Query createQuery(String hql, Object... parameters) {
        return setParameters(getSession().createQuery(hql), parameters);
    }

    /**
     * 根据实体类型创建Criteria
     *
     * @param entityClass
     * @param <T>
     * @return
     */
    public <T> Criteria createCriteria(Class<T> entityClass) {
        return getSession().createCriteria(getEntityClass(entityClass));
    }

    /**
     * 设置Query对象参数
     * 参数值可以是任意类型<br/>
     * 针对不同对象,处理情况不同,具体如下:
     * <ul>
     * <li>简单类型,包括基本类型以及String,Date,Number...,根据其索引位置为JPA参数 ?n 赋值</li>
     * <li>Map对象,根据key值将对应的value赋值给同名参数</li>
     * <li>POJO对象,根据属性名称进行同名参数赋值</li>
     * 对于简单类型的定义由实现类决定,以上所有类型均支持in操作
     * </ul>
     * 示例:
     * <pre>
     * <code>Map<String, Object> map = new HashMap<String, Object>();
     * map.put("idList", new Long[]{1L, 2L, 3L});
     * map.put("title", "%测试2%");
     * EventAlias event = new EventAlias();
     * event.setTitle("%测试%");
     * sql = "select e1.event_id ID,e1.event_date Date,e1.title,e1.type from event e1 where e1.title like :title or e1.title like ?1 and e1.event_id in :idList";
     * list = hibernateDao.sqlQuery(EventAlias.class, sql,event, "%中国%", map);</code>
     * </pre>
     * 另外,如果SQL(HQL)语句中只包含JDBC占位符的话,支持按索引位置赋值,但不支持in操作
     *
     * @param query
     * @param parameters
     * @param <T>
     * @return
     */
    private static <T extends Query> T setParameters(T query, Object... parameters) {
        if (query.getNamedParameters().length == 0) {
            for (int i = 0; i < parameters.length; i++) {
                query.setParameter(i, parameters[i]);
            }
            return query;
        }
        Map<String, Object> jpaMap = new HashMap<String, Object>();
        for (int i = 0; i < parameters.length; i++) {
            Object param = parameters[i];
            if (param == null || param instanceof Collection || isSimple(param) || param.getClass().isArray()) {
                jpaMap.put(String.valueOf(i), handle(param));
            } else if (param instanceof Map) {
                query.setProperties(namedParameters((Map) param));
            } else {
                query.setProperties(param);
            }
        }
        query.setProperties(jpaMap);
        return query;
    }


    /**
     * 设置Query对象类型
     *
     * @param query
     * @param beanClass
     * @return
     */
    private SQLQuery setQueryType(SQLQuery query, Class beanClass) {
        if (getClassMetadata(beanClass) != null) {
            query.addEntity(beanClass);
        } else {
            query.setResultTransformer(BeanResultTransFormer.get(beanClass));
        }
        return query;
    }

    /**
     * 将Map中的primitive类型数组转换为对象数组,以支持In操作
     *
     * @param map
     * @return
     */
    private static Map namedParameters(Map map) {
        for (Object o : map.entrySet()) {
            Entry en = (Entry) o;
            en.setValue(handle(en.getValue()));
        }
        return map;
    }

    /**
     * 获取实体对象的真实类型,避免Spring代理对象
     *
     * @param entity
     * @return
     */
    private static Class getEntityClass(Object entity) {
        if (entity instanceof Class) {
            return ClassUtils.getUserClass((Class) entity);
        }
        return ClassUtils.getUserClass(entity);
    }


    /**
     * @param entityClass 实体类型
     * @return
     */
    private ClassMetadata getClassMetadata(Class entityClass) {
        return sessionFactory.getClassMetadata(entityClass);
    }

    private static Object handle(Object value) {
        if (value != null && value.getClass().isArray()) {
            return ObjectUtils.toObjectArray(value);
        }
        return value;
    }

    private static boolean isSimple(Object value) {
        Class type = value.getClass();
        if (type.isArray() || simpleTypes.contains(type))
            return true;
        for (Class clazz : simpleTypes) {
            if (clazz.isInstance(value))
                return true;
        }
        return false;
    }

    static final Set<Class> simpleTypes = new HashSet<Class>();

    {
        //primitive和包装类
        simpleTypes.add(boolean.class);
        simpleTypes.add(byte.class);
        simpleTypes.add(byte[].class);
        simpleTypes.add(double.class);
        simpleTypes.add(float.class);
        simpleTypes.add(int.class);
        simpleTypes.add(long.class);
        simpleTypes.add(short.class);
        simpleTypes.add(Boolean.class);
        simpleTypes.add(Byte.class);
        simpleTypes.add(Double.class);
        simpleTypes.add(Float.class);
        simpleTypes.add(Integer.class);
        simpleTypes.add(Long.class);
        simpleTypes.add(Short.class);

        //常用简单类型
        simpleTypes.add(String.class);
        simpleTypes.add(BigDecimal.class);
        simpleTypes.add(BigInteger.class);
        simpleTypes.add(Number.class);
        simpleTypes.add(Date.class);
        simpleTypes.add(Time.class);
        simpleTypes.add(Timestamp.class);


        //数据对象类型
        simpleTypes.add(Blob.class);
        simpleTypes.add(Clob.class);
        simpleTypes.add(InputStream.class);
        simpleTypes.add(Reader.class);
        simpleTypes.add(Ref.class);
        simpleTypes.add(SQLXML.class);
        simpleTypes.add(URL.class);

        //类类型
        simpleTypes.add(Class.class);
    }
}
