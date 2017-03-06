/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package liang.dao.jdbc.common;

import liang.dao.jdbc.annotation.*;
import liang.dao.jdbc.ContentValues;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.util.*;
import java.util.Map.Entry;

/**
 * 增删改查的构造器，可以作为一个普通的类进行重用 不需要数据库连接就可以使用，只基于现有的JAVA类的分析
 *
 * @param <T>
 * @author
 */
public class CrudBuilder<T> {

    private static final Logger LOG = LoggerFactory.getLogger(CrudBuilder.class);
    private final Class<T> clazz;
    private final Audit audit;//审计的配置，如果为null，则表示不需要审计
    private List<Field> fieldList;//实体类需要和数据库交互的属性列表
//    private Map<String, EntityField> entityFieldMapping;//属性名到EntityField的映射
    private Field idField;//做为ID那一列的属性名，上面也包括ID列
    private String idName;//主键的列名
    private Sql insert, update, selectById, selectAll, delete;
    private String[] noAuditFields;//如果审计的话，那么此处就是不参与审计的字段列表
    private String[] titleFields;//如果审计的话，那么此处就是标记为标是的字段列表

    public CrudBuilder(Class<T> clazz) {
        this.clazz = clazz;
        this.audit = clazz.getAnnotation(Audit.class);
        initFields();
        initInsertSql();
        initSelectSql();
        initUpdateSql();
        initDeleteSql();
    }

    /**
     * 从类名到列表名的转换，默认实现和转换列名差不多，只是把
     * 可能在最后出现的Entity这几个字去掉
     *
     * @param className
     * @return
     */
    public static String convertToTableName(String className) {
        if (className.endsWith("Entity")) {
            className = className.substring(0, className.length() - 6);
        }
        return "t" + convertToColumnName(className);
    }

    /**
     * 从实体类的属性名到数据库列名的映射，默认实现是全部小写，
     * 然后大写字母前面加一个下划线，类似于
     * cityName -> city_name
     *
     * @param fieldName
     * @return
     */
    public static String convertToColumnName(String fieldName) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < fieldName.length(); i++) {
            char c = fieldName.charAt(i);
            if (Character.isUpperCase(c)) {
                sb.append("_");
                sb.append(Character.toLowerCase(c));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    /**
     * 根据属性，得到它对应的数据库的列名，此方法是先看有没有注解，如果有
     * 则取注解的，如果没有注解，则调用上面的方法转换成相应的数据库列名
     *
     * @param field
     * @return
     */
    public static String getColumnName(Field field) {
        Column column = field.getAnnotation(Column.class);
        if (column != null) {
            return column.name();
        }
        return convertToColumnName(field.getName());
    }

    public static String getFieldName(String columnName) {
        StringBuilder sb = new StringBuilder();
        boolean nextUpper = false;
        for (int i = 0; i < columnName.length(); i++) {
            char c = columnName.charAt(i);
            if (c == '_') {
                nextUpper = true;
                continue;
            }
            if (nextUpper) {
                nextUpper = false;
                sb.append(Character.toUpperCase(c));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    public boolean isNeedAudit() {
        return audit != null;
    }

    public Audit getAudit() {
        return audit;
    }

    public String[] getNoAuditFields() {
        return noAuditFields;
    }

    public String[] getTitleFields() {
        return titleFields;
    }

    public Class<T> getEntityClass() {
        return clazz;
    }

    private void initFields() {
        fieldList = new ArrayList<>();
        List<String> noAuditFieldList = new ArrayList<>();
        List<Pair<String, Integer>> titleList = new ArrayList<>();
        Class<?> cls = clazz;
        while (cls != Object.class) {
            Field[] fs = cls.getDeclaredFields();
            for (Field f : fs) {
                //先看看有没有@Transient的注释
                Transient t = f.getAnnotation(Transient.class);
                if (t != null) {
                    continue;
                }
                //再看看是不是static or final or transient的变量，如果是，也不管
                if (Modifier.isFinal(f.getModifiers())
                        || Modifier.isStatic(f.getModifiers())
                        || Modifier.isTransient(f.getModifiers())) {
                    continue;
                }
                fieldList.add(f);
                f.setAccessible(true);
                //如果有审计，则查看一下，是否排除了审计
                if (audit != null) {
                    NoAudit noAudit = f.getAnnotation(NoAudit.class);
                    if (noAudit != null) {
                        noAuditFieldList.add(f.getName());
                    }
                    Title title = f.getAnnotation(Title.class);
                    if (title != null) {
                        Pair<String, Integer> pair = new ImmutablePair<>(f.getName(), title.priority());
                        titleList.add(pair);
                    }
                }
                Id id = f.getAnnotation(Id.class);
                if (id != null) {
                    idName = id.name();
                    idField = f;
                }
            }
            cls = cls.getSuperclass();
        }

        if (audit != null) {
            noAuditFields = noAuditFieldList.toArray(new String[0]);
            if (CollectionUtils.isNotEmpty(titleList)) {
                Collections.sort(titleList, new Comparator<Pair<String, Integer>>() {

                    @Override
                    public int compare(Pair<String, Integer> o1, Pair<String, Integer> o2) {
                        int p1 = o1.getRight();
                        int p2 = o2.getRight();
                        if (p1 < p2) {
                            return -1;
                        } else if (p1 > p2) {
                            return 1;
                        } else {
                            return 0;
                        }
                    }
                });
                titleFields = new String[titleList.size()];
                for (int i = 0; i < titleList.size(); i++) {
                    titleFields[i] = titleList.get(i).getLeft();
                }
            }
        }

        //如果在最后还没有注解Id，那么就找有没有叫id的那一个属性，如果有，就是它了
        if (StringUtils.isBlank(idName)) {
            for (Field f : fieldList) {
                if (StringUtils.equalsIgnoreCase("id", f.getName())) {
                    idField = f;
                    Column column = f.getAnnotation(Column.class);
                    if (column != null) {
                        idName = column.name();
                    } else {
                        idName = "id";
                    }
                    LOG.warn("当前实体类:{}没有定义主键注解，将使用:{} 做为主键名", clazz, idName);
                }
            }
        }
        //如果还没有找到，那就抛异常了
        if (StringUtils.isBlank(idName)) {
            throw new IllegalArgumentException("当前实体类:{} 找不到主键");
        }
        //如果主键不是以Long型的，那就搞飞机了，肯定要抛异常了
        if (idField.getType() != Long.class) {
            throw new IllegalArgumentException("当前实体类:{} 主键类型不是Long，而是:" + idField.getType());
        }
    }

//    private void initEntityFieldMapping() {
//        entityFieldMapping = new HashMap<>();
//        for (Field field : fieldList) {
//            //id这一列不做初始化和判断
//            if (field == idField) {
//                continue;
//            }
//            String fieldName = field.getName();
//            //通过反射取到EntityField对象
//            //为了兼容老版代码，这里如果没有，那就算了，以后不能算了，以后要抛错
//            EntityField ef = getEntityField(fieldName);
//            if (ef != null) {
//                entityFieldMapping.put(fieldName, ef);
//            }
//        }
//    }

    private void initInsertSql() {
        StringBuilder sb = new StringBuilder();
        sb.append("insert into ").append(getTableName());
        sb.append(" (");
        sb.append(getFieldListString());
        sb.append(") values(");
        List<Field> fieldListLocal = fieldList;
        for (int i = 0; i < fieldListLocal.size(); i++) {
            if (i != 0) {
                sb.append(",");
            }
            sb.append("?");
        }
        sb.append(")");
        Sql sql = new Sql(sb.toString());
        this.insert = sql;
    }

    private void initUpdateSql() {
        StringBuilder set = new StringBuilder();
        int count = 0;
        List<Field> fieldListLocal = fieldList;
        Field idFieldLocal = idField;
        String idNameLocal = idName;
        for (Field f : fieldListLocal) {
            if (f == idFieldLocal) {
                continue;
            }
            if (count != 0) {
                set.append(",");
            }
            set.append("`").append(getColumnName(f)).append("`").append("=?");
            count++;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("update ").append(getTableName());
        sb.append(" set ").append(set);
        sb.append(" where ").append(idNameLocal).append("=?");
        Sql sql = new Sql(sb.toString());
        this.update = sql;
    }

    private void initSelectSql() {
        StringBuilder sb = new StringBuilder();
        sb.append("select ");
        sb.append(getFieldListString());
        sb.append(" from ").append(getTableName());
        sb.append(" where ").append(idName).append("=?");
        this.selectById = new Sql(sb.toString());
        sb.setLength(0);
        sb.append("select ");
        sb.append(getFieldListString());
        sb.append(" from ").append(getTableName());
        this.selectAll = new Sql(sb.toString());
    }

    private void initDeleteSql() {
        StringBuilder sb = new StringBuilder();
        sb.append("delete from ").append(getTableName());
        sb.append(" where ").append(idName).append(" =? ");
        this.delete = new Sql(sb.toString());
    }

    public Sql buildUpdateSql(ContentValues cv) {
        StringBuilder set = new StringBuilder();
        int count = 0;
        String idNameLocal = idName;
        Object idValue = null;
        List<Object> params = new ArrayList<>();
        for (Entry<String, Object> entry : cv.toFieldsMap().entrySet()) {
            if (StringUtils.equals(entry.getKey(), idNameLocal)) {
                idValue = entry.getValue();
                continue;
            }
            if (count != 0) {
                set.append(",");
            }
            set.append("`").append(getColumnName(entry.getKey())).append("`").append("=?");
            params.add(entry.getValue());
            count++;
        }
        //如果没有设置ID，则抛出异常
        if (idValue == null) {
            throw new IllegalArgumentException("找不到主键，无法进行更新");
        }

        StringBuilder sb = new StringBuilder();
        sb.append("update ").append(getTableName());
        sb.append(" set ").append(set);
        sb.append(" where ").append(idNameLocal).append("=?");
        Sql sql = new Sql(sb.toString());
        sql.addAllParams(params);
        sql.addParam(idValue);
        return sql;
    }

    public Sql buildInsertSql(ContentValues cv) {

        List<Object> params = new ArrayList<>();
        StringBuilder fields = new StringBuilder();
        StringBuilder placehodlers = new StringBuilder();
        int index = 0;
        for (Entry<String, Object> entry : cv.toFieldsMap().entrySet()) {
            if (index != 0) {
                fields.append(",");
                placehodlers.append(",");
            }
            fields.append("`").append(getColumnName(entry.getKey())).append("`");
            placehodlers.append("?");
            params.add(entry.getValue());
            index++;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("insert into ").append(getTableName());
        sb.append(" (").append(fields).append(")");
        sb.append(" values(").append(placehodlers).append(")");

        Sql sql = new Sql(sb.toString());
        sql.addAllParams(params);
        return sql;
    }

    public Field getIdField() {
        return idField;
    }

    public List<Field> getFieldList() {
        return Collections.unmodifiableList(fieldList);
    }

    public String getIdName() {
        return idName;
    }

    public Sql getInsertSql() {
        return insert.clone();
    }

    public Sql getUpdateSql() {
        return update.clone();
    }

    public Sql getSelectByIdSql() {
        return selectById.clone();
    }

    public Sql getSelectAllSql() {
        return selectAll.clone();
    }

    public Sql getDeleteSql() {
        return delete.clone();
    }

    public Sql buildSelect(SqlPath sqlPath) {
        if (sqlPath == null) {
            return Sql.from("select * from " + getTableName());
        } else {
            StringBuilder sql = new StringBuilder("select ");
            if (CollectionUtils.isEmpty(sqlPath.getSelectFields())) {
                sql.append("*");
            } else {
                for (int i = 0; i < sqlPath.getSelectFields().size(); i++) {
                    if (i > 0) {
                        sql.append(" , ");
                    }
                    sql.append(sqlPath.getSelectFields().get(i).toSqlString());
                }
            }

            sql.append(" from ");
            sql.append(getTableName());
            List<Object> params = new ArrayList<>(10);
            String alias = "";
            if (sqlPath.getNode() != null) {
                sql.append(" where ");
                sqlPath.getNode().toSqlString(alias, sql, params);
            }
            if (CollectionUtils.isNotEmpty(sqlPath.getOrders())) {
                sql.append(" order by ");
                boolean first = true;
                for (Order order : sqlPath.getOrders()) {
                    if (first) {
                        first = false;
                    } else {
                        sql.append(", ");
                    }
                    if (StringUtils.isNotBlank(alias)) {
                        sql.append(alias).append(".");
                    }
                    sql.append("`").append(getColumnName(order.getFieldName()))
                            .append("`").append(" ").append(order.getDirection());
                }
            }
            if (sqlPath.getPage() != null) {
                sql.append(" limit ").append(sqlPath.getPage().getOffset())
                        .append(",").append(sqlPath.getPage().getLimit());
            }
            return Sql.from(sql.toString(), params.toArray());
        }
    }

    public Sql buildSelect(List<SearchFilter> filters, PageRequest request) {
        List<Object> params = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        sb.append("select ");
        sb.append(getFieldListString());
        sb.append(" from ").append(getTableName());
        if (CollectionUtils.isNotEmpty(filters)) {
            buildConditions(null, sb, filters, params);
        }
        if (request != null) {
            List<PageRequest.Sort> sorts = request.getSort();
            if (CollectionUtils.isNotEmpty(sorts)) {
                sb.append(" order by ");
                for (PageRequest.Sort sort : sorts) {
                    sb.append(getColumnName(sort.getProperty())).append(" ").append(sort.getDirection()).append(",");
                }
                sb.deleteCharAt(sb.length() - 1);
            }
            sb.append(" limit ").append(request.getOffset());
            sb.append(",").append(request.getLimit());
        }
        Sql sql = new Sql(sb.toString());
        sql.addAllParams(params);
        return sql;
    }

    public Sql buildCount(List<SearchFilter> filters) {
        List<Object> params = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        sb.append("select count(*) from ").append(getTableName());
        if (CollectionUtils.isNotEmpty(filters)) {
            buildConditions(null, sb, filters, params);
        }
        Sql sql = new Sql(sb.toString());
        sql.addAllParams(params);
        return sql;
    }

    public Sql buildCount(SqlPath sqlPath) {
        StringBuilder sql = new StringBuilder("select count(*) from ").append(getTableName());
        List<Object> params = new ArrayList<>();
        String alias = "";
        if (sqlPath != null) {
            if (sqlPath.getNode() != null) {
                sql.append(" where ");
                sqlPath.getNode().toSqlString(alias, sql, params);
            }

        }
        return Sql.from(sql.toString(), params.toArray());
    }

    public List<Object> buildInsertParams(T t) {
        List<Field> fieldListLocal = fieldList;
        List<Object> params = new ArrayList<>();
        for (int i = 0; i < fieldListLocal.size(); i++) {
            try {
                params.add(fieldListLocal.get(i).get(t));
            } catch (IllegalArgumentException | IllegalAccessException ex) {
                LOG.error(null, ex);
            }
        }
        return params;
    }

    public List<Object> buildUpdateParams(T t) {
        List<Object> params = new ArrayList<>();
        List<Field> fieldListLocal = fieldList;
        Field idFieldLocal = idField;
        for (Field f : fieldListLocal) {
            if (f == idFieldLocal) {
                continue;
            }
            try {
                params.add(f.get(t));
            } catch (IllegalArgumentException | IllegalAccessException ex) {
                LOG.error(null, ex);
            }
        }

        try {
            params.add(idFieldLocal.get(t));
        } catch (IllegalArgumentException | IllegalAccessException ex) {
            LOG.error(null, ex);
        }
        return params;
    }

    public String getColumnName(String fieldName) {
        for (Field field : fieldList) {
            if (field.getName().equals(fieldName)) {
                return getColumnName(field);
            }
        }
        return convertToColumnName(fieldName);
    }

    /**
     * 返回当前实体类对应的表名，如果有Table的注解，则读取table的，否则就取类名，然后前面
     * 加t再小写
     *
     * @return
     */
    public String getTableName() {
        try {
            Table table = this.clazz.getAnnotation(Table.class);
            if (table != null) {
                return table.name();
            }
        } catch (Exception ex) {
            LOG.error(null, ex);
        }
        return convertToTableName(this.clazz.getSimpleName());
    }

    public String getFieldListString() {
        StringBuilder sb = new StringBuilder();
        int count = 0;
        for (Field f : fieldList) {
            if (count != 0) {
                sb.append(",");
            }
            sb.append("`").append(getColumnName(f)).append("`");
            count++;
        }
        return sb.toString();
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
    public T from(ResultSet rs) throws SQLException {
        try {
            T t = clazz.newInstance();
            //按照属性设置值，如果取不到会有异常，那么先算了
            for (Field f : getFieldList()) {
                try {
                    setFieldIfNeed(f, rs, t);
                } catch (Exception ex) {
                    LOG.debug("生成对象的时候，有列在结果集里面没有值:" + f.getName());
                    //ingore
                }
            }
            return t;
        } catch (Exception ex) {
            throw new SQLException(ex);
        }
    }

    private void setFieldIfNeed(Field f, ResultSet rs, T t) throws SQLException, IllegalArgumentException, IllegalAccessException {
        Class<?> type = f.getType();
        String columnName = getColumnName(f);
        if (type == String.class) {
            String value = rs.getString(columnName);
            f.set(t, value);
        } else if (type == int.class) {
            int value = rs.getInt(columnName);
            f.setInt(t, value);
        } else if (type == long.class) {
            long value = rs.getLong(columnName);
            f.setLong(t, value);
        } else if (type == float.class) {
            float value = rs.getFloat(columnName);
            f.setFloat(t, value);
        } else if (type == double.class) {
            double value = rs.getDouble(columnName);
            f.setDouble(t, value);
        } else if (type == byte.class) {
            byte value = rs.getByte(columnName);
            f.setByte(t, value);
        } else if (type == short.class) {
            short value = rs.getShort(columnName);
            f.setShort(t, value);
        } else if (type == boolean.class) {
            boolean value = rs.getBoolean(columnName);
            f.setBoolean(t, value);
        } else if (java.sql.Date.class == type) {
            java.sql.Date value = rs.getDate(columnName);
            f.set(t, value);
        } else if (Time.class == type) {
            Time value = rs.getTime(columnName);
            f.set(t, value);
        } else if (Date.class.isAssignableFrom(type)) {
            Date value = rs.getTimestamp(columnName);
            f.set(t, value);
        } else {
            Object value = rs.getObject(columnName);
            f.set(t, value);
        }
    }

    public void buildConditions(String alias, StringBuilder sb, List<SearchFilter> filters, List<Object> params) {
        if (CollectionUtils.isEmpty(filters)) {
            return;
        }
        sb.append(" where ");
        for (int i = 0; i < filters.size(); i++) {
            SearchFilter filter = filters.get(i);
            if (i > 0) {
                sb.append(" and ");
            }
            String columnName = "`" + getColumnName(filter.fieldName) + "`";
            if (StringUtils.isNotBlank(alias)) {
                columnName = alias + "." + columnName;
            }
            switch (filter.operator) {
                case EQ:
                    sb.append(columnName).append(" =?");
                    params.add(filter.value);
                    break;
                case NEQ:
                    sb.append(columnName).append(" <>?");
                    params.add(filter.value);
                    break;
                case LIKE:
                    sb.append(columnName).append(" like ?");
                    params.add("%" + filter.value + "%");
                    break;
                case CUSTOM_LIKE:
                    sb.append(columnName).append(" like ?");
                    params.add(filter.value.toString());
                    break;
                case NLIKE:
                    sb.append(columnName).append(" not like ?");
                    params.add("%" + filter.value + "%");
                    break;
                case GT:
                    sb.append(columnName).append(" >?");
                    params.add(filter.value);
                    break;
                case LT:
                    sb.append(columnName).append(" <?");
                    params.add(filter.value);
                    break;
                case GTE:
                    sb.append(columnName).append(" >=?");
                    params.add(filter.value);
                    break;
                case LTE:
                    sb.append(columnName).append(" <=?");
                    params.add(filter.value);
                    break;
                case ISNULL:
                    sb.append(columnName).append(" is null");
                    break;
                case IS_NOT_NULL:
                    sb.append(columnName).append(" is not null");
                    break;
                case STARTING:
                    sb.append(columnName).append(" like ?");
                    params.add(filter.value + "%");
                    break;
                case ENDING:
                    sb.append(columnName).append(" like ?");
                    params.add("%" + filter.value);
                    break;
                case LENGTH_EQ:
                    sb.append(" length(").append(columnName).append(")=?");
                    params.add(filter.value);
                    break;
                case LENGTH_NEQ:
                    sb.append(" length(").append(columnName).append(")<>?");
                    params.add(filter.value);
                    break;
                case BIT_AND:
                    sb.append(" ").append(columnName).append("&?=?");
                    params.add(filter.value);
                    params.add(filter.value);
                    break;
                case IN:


                    String[] ss;
                    if (filter.value == null) {
                        throw new RuntimeException("in 条件里不可以为空");
                    } else if (filter.value instanceof List) {
                        @SuppressWarnings(value = "unchecked")
                        List<Object> lo = (List<Object>) filter.value;
                        ss = new String[lo.size()];
                        for (int index = 0; index < lo.size(); index++) {
                            ss[index] = String.valueOf(lo.get(index));
                        }
                    } else if (filter.value instanceof Set) {
                        @SuppressWarnings(value = "unchecked")
                        Set<Object> so = (Set<Object>) filter.value;
                        ss = new String[so.size()];
                        int index = 0;
                        for (Object v : so) {
                            ss[index++] = String.valueOf(v);
                        }
                    } else if (filter.value.getClass().isArray()) {
                        Object[] os = (Object[]) filter.value;
                        ss = new String[os.length];
                        for (int index = 0; index < os.length; index++) {
                            ss[index] = String.valueOf(os[index]);
                        }
                    } else {
                        ss = String.valueOf(filter.value).split(",");
                    }

                    if (ss.length > 0) {
                        sb.append(columnName).append(" in (");
                        for (int index = 0; index < ss.length; index++) {
                            if (index > 0) {
                                sb.append(" , ");
                            }
                            sb.append("?");
                            params.add(ss[index]);
                        }
                        sb.append(")");
                    } else {
                        throw new RuntimeException("in条件里至少要有一个值");
                    }
                    break;
            }
        }
    }
}
