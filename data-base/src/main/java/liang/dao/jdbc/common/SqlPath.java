package liang.dao.jdbc.common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * <b>SqlPath 设计用来简化单表查询</b><br>
 * <b>示例1:</b>(TableEntity.Fields.xx字段进行静态引入) <br>
 * SQL:<code>select * from t_dict where name like '%王%' and (status = 1 or status = 0)
 * order by score desc limit 0,10</code>;<br>
 * SqlPath:<code> dictEntityDao.findAll(name.like("王").and(status.eq(1).or(status.eq(0)))
 * .desc(score).limit(0,10)</code>;<br>
 * <br>
 * <b>示例2:</b><br>
 * 如果只有order by id limit 0,10 这样的sql,那么应该使用
 * SqlPath.orderBy().asc(id).limit(0,10),<br>
 * 这种情况下不应该再进行条件拼接,否则会报错.<br>
 * <b>示例3:</b><br>
 * 如果SqlPath是动态生成且不确定第一个条件时请这样进行处理<br>
 * <code>if(sqlPath == null) sqlPath = field.eq(1); else sqlPath.and(field.eq(1))</code><br>
 * <br>
 * btw:使用前最好将XXXEntity.Fields.xxx字段进行静态导入使用,不导入使用会显得代码较长<br>
 * 复杂查询请使用Sql直接查询,当SqlPath中条件较多时会有大量嵌套,代码可读性变差
 *
 * @author
 */
public class SqlPath {

    public static SqlPath where(String field, SearchFilter.Operator op, Object value) {
        return new SqlPath(Node.create(field, op, value));
    }

    public static SqlPath orderBy() {

        return new SqlPath();
    }

    private Node node = null;
    private List<Order> orders = null;
    private Page page = null;

    private List<SqlField> selectFields;

    private SqlPath() {

    }

    public static SqlPath select(String... fields) {
        SqlPath sqlPath = new SqlPath();
        if (fields.length > 0) {
            sqlPath.selectFields = new ArrayList<>(10);
            for (String f : fields) {
                sqlPath.selectFields.add(new SqlField(f));
            }
        }
        return sqlPath;
    }

    public SqlPath(Node node) {
        this.node = node;
    }

    public SqlPath and(String field, SearchFilter.Operator op, Object value) {
        if (this.node == null) {
            this.node = Node.create(field, op, value);
        } else {
            this.node = new Nodes(this.node, PathType.AND, Node.create(field, op, value));
        }
        return this;
    }

    public SqlPath or(String field, SearchFilter.Operator op, Object value) {
        if (this.node == null) {
            this.node = Node.create(field, op, value);
        } else {
            this.node = new Nodes(this.node, PathType.OR, Node.create(field, op, value));
        }
        return this;
    }

    public SqlPath and(SqlPath cnd) {
        if (cnd.node != null) {
            if (this.node == null) {
                this.node = cnd.node;
            } else {
                this.node = new Nodes(this.node, PathType.AND, cnd.node);
            }
        }
        return this;
    }

    public SqlPath or(SqlPath cnd) {
        if (cnd.node != null) {
            if (this.node == null) {
                this.node = cnd.node;
            } else {
                this.node = new Nodes(this.node, PathType.OR, cnd.node);
            }
        }
        return this;
    }

    public SqlPath asc(String fieldName) {
        if (orders == null) {
            orders = new ArrayList<>(5);
        }
        orders.add(new Order(fieldName, "ASC"));
        return this;
    }

    public SqlPath desc(String fieldName) {
        if (orders == null) {
            orders = new ArrayList<>(5);
        }
        orders.add(new Order(fieldName, "DESC"));
        return this;
    }

    public SqlPath page(PageRequest request) {
        limit(request.getOffset(), request.getLimit());
        List<PageRequest.Sort> sortList = request.getSort();
        if (sortList != null) {
            for (PageRequest.Sort sort : sortList) {
                switch (sort.getDirection()) {
                    case DESC:
                        desc(sort.getProperty());
                        break;
                    case ASC:
                        asc(sort.getProperty());
                        break;
                }
            }
        }
        return this;
    }

    /**
     * 分页参数
     *
     * @param pageNumber 从1开始，表示第一页
     * @param pageSize   每页的大小
     * @return this
     */
    public SqlPath page(int pageNumber, int pageSize) {
        limit( (pageNumber - 1L) * pageSize, pageSize);
        return this;
    }

    /**
     * 分页参数
     *
     * @param pageSize 每页的大小
     * @return
     */
    public SqlPath page(int pageSize) {
        page(1, pageSize);
        return this;
    }

    public SqlPath limit(long limit) {
        limit(0, limit);
        return this;
    }

    /**
     * 分页参数,这里的offset 就是 sql中limit 0,1 中的0,
     * offset 不是 page, offset和page的关系为 offset = (page-1)*limit
     *
     * @param offset
     * @param limit
     * @return
     */
    public SqlPath limit(long offset, long limit) {
        if (page == null) {
            page = new Page();
        }
        page.setOffset(offset);
        page.setLimit(limit);
        return this;
    }

    public Node getNode() {
        return node;
    }

    public List<Order> getOrders() {
        if (orders != null) {
            return Collections.unmodifiableList(orders);
        } else {
            return null;
        }
    }

    public Page getPage() {
        return page;
    }

    public List<SqlField> getSelectFields() {
        return selectFields;
    }

}
