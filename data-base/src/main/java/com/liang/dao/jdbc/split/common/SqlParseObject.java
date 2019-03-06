package com.liang.dao.jdbc.split.common;

import com.liang.dao.jdbc.split.SqlConstants.DML;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by liangzhiyan on 2017/6/30.
 */
public class SqlParseObject {

    /************************分表相关**********************/
    private String table;
    private int tableIndex;
    private String realTableName;
    private Map<String, Integer> whereColumnIndexMap = new HashMap<>();
    private Map<String, Integer> insertColumnIndexMap = new HashMap<>();

    private DML dml;
    private boolean isHaveWhereCondition;

    /*********************分库相关*************************/
    private String dbSource;
    private int dbSourceIndex;
    private String realDbSource;

    public String getTable() {
        return table;
    }

    public SqlParseObject setTable(String table) {
        this.table = table;
        return this;
    }

    public int getTableIndex() {
        return tableIndex;
    }

    public SqlParseObject setTableIndex(int tableIndex) {
        this.tableIndex = tableIndex;
        return this;
    }

    public String getRealTableName() {
        return realTableName;
    }

    public SqlParseObject setRealTableName(String realTableName) {
        this.realTableName = realTableName;
        return this;
    }

    public Map<String, Integer> getWhereColumnIndexMap() {
        return whereColumnIndexMap;
    }

    public SqlParseObject setWhereColumnIndexMap(Map<String, Integer> whereColumnIndexMap) {
        this.whereColumnIndexMap = whereColumnIndexMap;
        return this;
    }

    public Map<String, Integer> getInsertColumnIndexMap() {
        return insertColumnIndexMap;
    }

    public SqlParseObject setInsertColumnIndexMap(Map<String, Integer> insertColumnIndexMap) {
        this.insertColumnIndexMap = insertColumnIndexMap;
        return this;
    }

    public DML getDml() {
        return dml;
    }

    public SqlParseObject setDml(DML dml) {
        this.dml = dml;
        return this;
    }

    public boolean isHaveWhereCondition() {
        return isHaveWhereCondition;
    }

    public SqlParseObject setHaveWhereCondition(boolean haveWhereCondition) {
        isHaveWhereCondition = haveWhereCondition;
        return this;
    }

    public String getDbSource() {
        return dbSource;
    }

    public SqlParseObject setDbSource(String dbSource) {
        this.dbSource = dbSource;
        return this;
    }

    public int getDbSourceIndex() {
        return dbSourceIndex;
    }

    public SqlParseObject setDbSourceIndex(int dbSourceIndex) {
        this.dbSourceIndex = dbSourceIndex;
        return this;
    }

    public String getRealDbSource() {
        return realDbSource;
    }

    public SqlParseObject setRealDbSource(String realDbSource) {
        this.realDbSource = realDbSource;
        return this;
    }
}
