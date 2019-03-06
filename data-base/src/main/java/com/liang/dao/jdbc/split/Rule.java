package com.liang.dao.jdbc.split;

import com.liang.common.exception.ParameterException;
import org.apache.commons.lang3.StringUtils;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by liangzhiyan on 2017/6/9.
 */
public class Rule {
    private String columnName;
    private String rule;
    private String index;
    private Set<Integer> indexSet = new HashSet<>();
    private String ruleType;
    private int modValue;
    private int addValue;

    public String getColumnName() {
        return columnName;
    }

    public Rule setColumnName(String columnName) {
        this.columnName = columnName;
        return this;
    }

    public String getRule() {
        return rule;
    }

    public Rule setRule(String rule) {
        this.rule = rule;
        return this;
    }

    public String getIndex() {
        return index;
    }

    public Rule setIndex(String index) {
        this.index = index;
        if (StringUtils.startsWith(index, "[") && StringUtils.endsWith(index, "]")) {
            String[] indexArray = index.replace("[", "").replace("]", "").split(",");
            if (indexArray.length != 2) {
                throw ParameterException.throwException("index 格式不正确，正确格式为：[xx,xx]");
            }
            for (int i = Integer.parseInt(indexArray[0]); i <= Integer.parseInt(indexArray[1]); i++) {
                indexSet.add(i);
            }
        } else {
            throw ParameterException.throwException("index 格式不正确，正确格式为：[xx,xx]");
        }
        return this;
    }

    public Set<Integer> getIndexSet() {
        return indexSet;
    }

    public Rule setIndexSet(Set<Integer> indexSet) {
        this.indexSet = indexSet;
        return this;
    }

    public String getRuleType() {
        return ruleType;
    }

    public Rule setRuleType(String ruleType) {
        this.ruleType = ruleType;
        return this;
    }

    public int getModValue() {
        return modValue;
    }

    public Rule setModValue(int modValue) {
        this.modValue = modValue;
        return this;
    }

    public int getAddValue() {
        return addValue;
    }

    public Rule setAddValue(int addValue) {
        this.addValue = addValue;
        return this;
    }
}
