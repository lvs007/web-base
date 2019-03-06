/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.liang.dao.jdbc.common;

/**
 * 搜索的过滤器，此为过滤条件的一个
 *
 * @author
 */
public class SearchFilter {

    public enum Operator {

        EQ, NEQ, LIKE, NLIKE, CUSTOM_LIKE, GT, LT, GTE, LTE, ISNULL, IS_NOT_NULL, STARTING, ENDING, LENGTH_EQ, LENGTH_NEQ, IN, BIT_AND
    }
    public String fieldName;
    public Operator operator;
    public Object value;

    public SearchFilter(String fieldName, Operator operator, Object value) {
        this.fieldName = fieldName;
        this.value = value;
        this.operator = operator;
    }

}
