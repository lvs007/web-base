package liang.dao.jdbc.split;

import org.apache.commons.collections4.CollectionUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by liangzhiyan on 2017/6/9.
 */
public class TableRule {
    private String tableName;
    private List<Rule> ruleList;
    private Set<Integer> indexSet = new HashSet<>();
    private Set<Integer> scanIndexSet = new HashSet<>();

    public String getTableName() {
        return tableName;
    }

    public TableRule setTableName(String tableName) {
        this.tableName = tableName;
        return this;
    }

    public List<Rule> getRuleList() {
        return ruleList;
    }

    public TableRule setRuleList(List<Rule> ruleList) {
        this.ruleList = ruleList;
        for (Rule rule : ruleList) {
            indexSet.addAll(rule.getIndexSet());
            if (CollectionUtils.isEmpty(scanIndexSet)) {
                scanIndexSet.addAll(rule.getIndexSet());
            } else if (scanIndexSet.size() > rule.getIndexSet().size()) {
                scanIndexSet = rule.getIndexSet();
            }
        }
        return this;
    }

    public Set<Integer> getIndexSet() {
        return indexSet;
    }

    public TableRule setIndexSet(Set<Integer> indexSet) {
        this.indexSet = indexSet;
        return this;
    }

    public Set<Integer> getScanIndexSet() {
        return scanIndexSet;
    }

    public TableRule setScanIndexSet(Set<Integer> scanIndexSet) {
        this.scanIndexSet = scanIndexSet;
        return this;
    }
}
