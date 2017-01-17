package liang.dao.common;

import liang.dao.hibernate.HibernateDao;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 针对实体类型的DAO,所有针对具体实体类型的DAO均应继承自该类
 *
 * @author skyfalling
 */
public abstract class EntityDao<T> {

    private Class<T> entityClass;
    @Resource(name = "hibernateDao")
    private HibernateDao dao;

    public EntityDao(Class<T> entityClass) {
        this.entityClass = entityClass;
    }


    public T get(T entity) {
        return dao.get(entity);
    }


    public <K extends Serializable> T get(K id) {
        return dao.get(entityClass, id);
    }


    public List<T> getAll() {
        return dao.getAll(entityClass);
    }


    public <K extends Serializable> List<T> getByIds(List<K> ids) {
        return dao.getByIds(entityClass, ids);
    }


    public List<T> getByProperties(Map<String, Object> properties) {
        return dao.getByProperties(entityClass, properties);
    }


    public void delete(T entity) {
        dao.delete(entity);
    }


    public <K extends Serializable> void delete(K id) {
        dao.delete(entityClass, id);
    }


    public T save(T entity) {
        return dao.save(entity);
    }


    public void save(List<T> entities) {
        dao.save(entities);
    }


    protected List<T> sqlQuery(String sql, Object... parameters) {
        return dao.sqlQuery(entityClass, sql, parameters);
    }


    protected Page<T> sqlPageQuery(Page<T> page, String sql, Object... parameters) {
        return dao.sqlPageQuery(page, entityClass, sql, parameters);
    }


    protected List hqlQuery(String hql, Object... parameters) {
        return dao.hqlQuery(hql, parameters);
    }


    protected Page hqlPageQuery(Page page, String hql, Object... parameters) {
        return dao.hqlPageQuery(page, hql, parameters);
    }


    protected void executeBatch(String[] sqlList) {
        dao.executeBatch(sqlList);
    }


    protected void executeBatch(String sql, Object[]... parameters) {
        dao.executeBatch(sql, parameters);
    }


    protected Page pageQuery(Query query, Page page) {
        return HibernateDao.pageQuery(query, page);
    }


    protected SQLQuery createSQLQuery(String sql, Object... parameters) {
        return dao.createSQLQuery(sql, parameters);
    }


    protected SQLQuery createSQLQuery(SqlStatement sqlStatement) {
        return dao.createSQLQuery(sqlStatement);
    }

    protected Query createQuery(String hql, Object... parameters) {
        return dao.createQuery(hql, parameters);
    }

    protected <T> Criteria createCriteria() {
        return dao.createCriteria(entityClass);
    }

}
