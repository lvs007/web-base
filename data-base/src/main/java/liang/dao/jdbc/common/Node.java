/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package liang.dao.jdbc.common;

import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Set;

/**
 * 单个sql条件如 id = 1
 *
 * @author
 */
public class Node {

    public static Node create(String field, SearchFilter.Operator op, Object value) {
        Node n = new Node();
        n.setField(field);
        n.setOperator(op);
        n.setValue(value);
        return n;
    }

    private String field;
    private SearchFilter.Operator operator;
    private Object value;

    public void toSqlString(String alias, StringBuilder sb, List<Object> params) {
        String columnName = "`" + FieldUtils.convertToColumnName(getField()) + "`";
        if (StringUtils.isNotBlank(alias)) {
            columnName = alias + "." + columnName;
        }
        switch (getOperator()) {
            case EQ:
                sb.append(columnName).append(" =?");
                params.add(getValue());
                break;
            case NEQ:
                sb.append(columnName).append(" <>?");
                params.add(getValue());
                break;
            case LIKE:
                sb.append(columnName).append(" like ?");
                params.add("%" + getValue() + "%");
                break;
            case CUSTOM_LIKE:
                sb.append(columnName).append(" like ?");
                params.add(getValue().toString());
                break;
            case NLIKE:
                sb.append(columnName).append(" not like ?");
                params.add("%" + getValue() + "%");
                break;
            case GT:
                sb.append(columnName).append(" >?");
                params.add(getValue());
                break;
            case LT:
                sb.append(columnName).append(" <?");
                params.add(getValue());
                break;
            case GTE:
                sb.append(columnName).append(" >=?");
                params.add(getValue());
                break;
            case LTE:
                sb.append(columnName).append(" <=?");
                params.add(getValue());
                break;
            case ISNULL:
                sb.append(columnName).append(" is null");
                break;
            case IS_NOT_NULL:
                sb.append(columnName).append(" is not null");
                break;
            case STARTING:
                sb.append(columnName).append(" like ?");
                params.add(getValue() + "%");
                break;
            case ENDING:
                sb.append(columnName).append(" like ?");
                params.add("%" + getValue());
                break;
            case LENGTH_EQ:
                sb.append(" length(").append(columnName).append(")=?");
                params.add(getValue());
                break;
            case LENGTH_NEQ:
                sb.append(" length(").append(columnName).append(")<>?");
                params.add(getValue());
                break;
            case BIT_AND:
                sb.append(" ").append(columnName).append("&?=?");
                params.add(getValue());
                params.add(getValue());
                break;
            case IN:
                String[] ss;
                if (getValue() == null) {
                    throw new RuntimeException("in 条件里不可以为空");
                } else if (getValue() instanceof List) {
                    @SuppressWarnings(value = "unchecked")
                    List<Object> lo = (List<Object>) getValue();
                    ss = new String[lo.size()];
                    for (int i = 0; i < lo.size(); i++) {
                        ss[i] = String.valueOf(lo.get(i));
                    }
                } else if (getValue() instanceof Set) {
                    @SuppressWarnings(value = "unchecked")
                    Set<Object> so = (Set<Object>) getValue();
                    ss = new String[so.size()];
                    int index = 0;
                    for (Object v : so) {
                        ss[index++] = String.valueOf(v);
                    }
                } else if (getValue().getClass().isArray()) {
                    Object[] os = (Object[]) getValue();
                    ss = new String[os.length];
                    for (int i = 0; i < os.length; i++) {
                        ss[i] = String.valueOf(os[i]);
                    }
                } else {
                    ss = String.valueOf(getValue()).split(",");
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

    /**
     * @return the field
     */
    public String getField() {
        return field;
    }

    /**
     * @param field the field to set
     */
    public void setField(String field) {
        this.field = field;
    }

    /**
     * @return the operator
     */
    public SearchFilter.Operator getOperator() {
        return operator;
    }

    /**
     * @param operator the operator to set
     */
    public void setOperator(SearchFilter.Operator operator) {
        this.operator = operator;
    }

    /**
     * @return the value
     */
    public Object getValue() {
        return value;
    }

    /**
     * @param value the value to set
     */
    public void setValue(Object value) {
        this.value = value;
    }

}
