package com.liang.dao.common;


import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 数据访问接口定义<br/>
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
public interface Dao {

    /**
     * 根据ID查询实体对象<br/>
     * 这里要求entity对象的ID属性值不能为空
     *
     * @param entity
     * @param <T>
     * @return
     */
    <T> T get(T entity);

    /**
     * 根据ID查询实体对象
     *
     * @param entityClass
     * @param id
     * @param <T>
     * @param <K>
     * @return
     */
    <T, K extends Serializable> T get(Class<T> entityClass, K id);

    /**
     * 获取指定类型的全部实体对象
     *
     * @param entityClass
     * @param <T>
     * @return
     */
    <T> List<T> getAll(Class<T> entityClass);

    /**
     * 根据ID列表查询实体对象
     *
     * @param entityClass
     * @param ids         ID列表
     * @param <T>
     * @param <K>
     * @return
     */
    <T, K extends Serializable> List<T> getByIds(Class<T> entityClass, List<K> ids);


    /**
     * 根据键值对获取实体对象列表<br/>
     * 这里key为属性名称,value为属性值
     *
     * @param entityClass
     * @param properties
     * @param <T>
     * @return
     */
    <T> List<T> getByProperties(Class<T> entityClass, Map<String, Object> properties);


    /**
     * 删除实体对象
     *
     * @param entity
     * @param <T>
     */
    <T> void delete(T entity);

    /**
     * 根据ID删除实体对象
     *
     * @param entityClass
     * @param id
     * @param <K>
     */
    <K extends Serializable> void delete(Class entityClass, K id);


    /**
     * 保存实体对象
     *
     * @param entity
     * @param <T>
     * @return
     */
    <T> T save(T entity);

    /**
     * 批量保存实体对象
     *
     * @param entities
     * @param <T>
     */
    <T> void save(List<T> entities);


    /**
     * 执行SQL查询,返回Map对象列表List&lt;Map><br/>
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
     * @param sql
     * @param parameters
     * @return
     */
    List<Map<String, ?>> sqlQuery(String sql, Object... parameters);


    /**
     * 执行SQL查询,返回指定类型的实体列表List&lt;T><br/>
     *
     * @param beanClass
     * @param sql
     * @param parameters
     * @param <T>
     * @return
     * @see #sqlQuery(String, Object...)
     */
    <T> List<T> sqlQuery(Class<T> beanClass, String sql, Object... parameters);


    /**
     * 执行SQL分页查询,返回Map对象列表List&lt;Map><br/>
     *
     * @param sql
     * @param pageNo
     * @param pageSize
     * @param parameters
     * @return
     * @see #sqlQuery(String, Object...)
     */
    List<Map<String, ?>> sqlPageQuery(String sql, int pageNo, int pageSize, Object... parameters);


    /**
     * 执行SQL分页查询,,返回指定类型的实体列表List&lt;T><br/>
     *
     * @param beanClass
     * @param sql
     * @param pageNo
     * @param pageSize
     * @param parameters
     * @return
     * @see #sqlQuery(String, Object...)
     */
    <T> List<T> sqlPageQuery(Class<T> beanClass, String sql, int pageNo, int pageSize, Object... parameters);

    /**
     * 批量执行SQL语句
     *
     * @param sqlList
     */
    void executeBatch(String[] sqlList);

    /**
     * 批量执行带参数的SQL语句
     *
     * @param sql
     * @param parameters
     */
    void executeBatch(String sql, Object[]... parameters);

    <T> Page<T> sqlPageQuery(Page<T> page, Class<T> beanClass, String sql, Object... parameters);

    Page<Map<String, ?>> sqlPageQuery(Page page, String sql, Object... parameters);
}
