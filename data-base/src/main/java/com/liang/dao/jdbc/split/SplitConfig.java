package com.liang.dao.jdbc.split;

import com.liang.common.exception.ParameterException;
import com.liang.dao.jdbc.split.common.CommonBaseConfig;
import com.liang.dao.jdbc.split.common.SplitTableMethod;
import com.liang.dao.jdbc.split.common.SqlCommonUtil;
import com.liang.dao.jdbc.split.db.DbConfig;
import com.liang.dao.jdbc.split.service.CreateTable;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * Created by liangzhiyan on 2017/6/22.
 */
@Component
public class SplitConfig {

    private static final Logger LOG = LoggerFactory.getLogger(SplitConfig.class);

    private Map<String, TableRule> tableRuleMap;

    private ScriptEngine engine;

    private Properties properties;

    private ThreadPoolTaskExecutor splitTableExecutor;

    @Autowired
    private CreateTable createTable;

    @Autowired(required = false)
    @Qualifier(value = "splitTableMethod")
    private SplitTableMethod splitTableMethod;

    private SplitConfig() {
    }

    @PostConstruct
    private void init() {
        LOG.info("Begin 初始化ParseSql");
        SqlCommonUtil.setSplitTableMethod(splitTableMethod);
        initProperties();
        if (isSplitTable()) {
            initScriptEngine();
            parseConfig();
            createTable();
            initSplitTableExecutor();
            loadCommonConfig();
        }
        LOG.info("End 初始化ParseSql");
    }

    private void initSplitTableExecutor() {
        if (splitTableExecutor == null) {
            splitTableExecutor = new ThreadPoolTaskExecutor();
            splitTableExecutor.setCorePoolSize(8);
            splitTableExecutor.setMaxPoolSize(128);
            splitTableExecutor.setAllowCoreThreadTimeOut(true);
            splitTableExecutor.setThreadGroupName("split-table-exe");
            splitTableExecutor.setDaemon(true);
            splitTableExecutor.initialize();
        }
    }

    private void initProperties() {
        InputStream inputStream = getClass().getResourceAsStream("/split-rule.properties");
        properties = new Properties();
        if (inputStream != null) {
            try {
                properties.load(inputStream);
            } catch (IOException e) {
                LOG.error("加载分表配置文件出错", e);
                throw new RuntimeException(e);
            }
        } else {
            LOG.info("没有split-rule.properties配置文件");
        }
    }

    private void initScriptEngine() {
        ScriptEngineManager factory = new ScriptEngineManager();
        //每次生成一个engine实例
        engine = factory.getEngineByName("js");
    }

    private void createTable() {
        if (isCreateTable()) {
            createTable.createIdTable();
            for (String table : tableRuleMap.keySet()) {
                createTable.createSplitTable(table, tableRuleMap.get(table).getIndexSet());
            }
            createTable.createSqlExeFailTable();
        }
    }

    private void loadCommonConfig() {
        CommonBaseConfig.setIsCreateTable(isCreateTable());
        CommonBaseConfig.setIsPrintLog(isPrintLog());
        CommonBaseConfig.setIsSplitTable(isSplitTable());
        CommonBaseConfig.setIsThrowEx(isThrowEx());
        CommonBaseConfig.setIsSplitDb(isSplitDb());
    }

    public boolean isSplitTable() {
        String splitTable = properties.getProperty("split.table", "false");
        if (StringUtils.equalsIgnoreCase(splitTable, "true")) {
            return true;
        }
        return false;
    }

    public boolean isSplitDb() {
        String splitTable = properties.getProperty("split.db", "false");
        if (StringUtils.equalsIgnoreCase(splitTable, "true")) {
            DbConfig.setIsSplitDb(true);
            return true;
        }
        return false;
    }

    public boolean isCreateTable() {
        if (StringUtils.equalsIgnoreCase(properties.getProperty("split.createTable", "false"), "true")) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 是否打印日志
     *
     * @return
     */
    public boolean isPrintLog() {
        String isPrintLog = properties.getProperty("split.printlog", "false");
        if (StringUtils.equalsIgnoreCase(isPrintLog, "true")) {
            return true;
        }
        return false;
    }

    /**
     * 如果sql语句不包含分表维度，是否抛出异常
     *
     * @return
     */
    public boolean isThrowEx() {
        String isThrowEx = properties.getProperty("split.throwEx", "false");
        if (StringUtils.equalsIgnoreCase(isThrowEx, "true")) {
            return true;
        }
        return false;
    }

    /**
     * split.createTable=false
     * split.table=true//是否分表
     * split.tableName[0]=account//表名前缀
     * split.tableName[0].rule[0].columnName=user_name//列名
     * split.tableName[0].rule[0].rule=user_name%10//分表规则
     * split.tableName[0].rule[0].index=[0,9]//对应表的下标
     * split.tableName[0].rule[1].columnName=id
     * split.tableName[0].rule[1].rule=id%10+10
     * split.tableName[0].rule[1].index=[10,19]
     * <p>
     * 额外字段配置：
     * split.printlog=true//是否打印日志
     * split.throwEx=true//如果sql语句不包含分表维度，是否抛出异常
     * <p>
     * 如果需要高效率的index解析请用：
     * split.tableName[0].rule[0].ruleType=hashCode或者value，hashCode表示使用值的hash值，value表示用原值
     * split.tableName[0].rule[0].modValue=10，表示值%10
     * split.tableName[0].rule[0].addValue=10，表示求%后+10
     * 如果需要分库：
     * split.db=false//是否分库
     * split.tableName[0].rule[0].dbRule=db1:[0-3],db2:[4-6],db3:[7-9]
     * split.tableName[0].rule[1].dbRule=db1:[10-13],db2:[14-16],db3:[17-19]
     */
    private void parseConfig() {
        tableRuleMap = new HashMap<>();
        String tableNameTemplate = "split.tableName[?]";
        String ruleTemplate = ".rule[?].";
        String columnNameTemp = "columnName";
        String ruleTemp = "rule";
        String indexTemp = "index";
        String ruleTypeTemp = "ruleType";
        String modValueTemp = "modValue";
        String addValueTemp = "addValue";
        String dbRuleTemp = "dbrule";
        int tableNameIndex = 0;
        while (true) {
            String tableNameTemplateReplace = tableNameTemplate.replace("?", "" + tableNameIndex);
            String tableName = properties.getProperty(tableNameTemplateReplace);
            if (StringUtils.isNotBlank(tableName)) {
                int ruleIndex = 0;
                List<Rule> ruleList = new ArrayList<>();
                while (true) {
                    String ruleTemplateReplace = ruleTemplate.replace("?", "" + ruleIndex);
                    String columnName = properties.getProperty(tableNameTemplateReplace + ruleTemplateReplace + columnNameTemp);
                    String rule = properties.getProperty(tableNameTemplateReplace + ruleTemplateReplace + ruleTemp);
                    String index = properties.getProperty(tableNameTemplateReplace + ruleTemplateReplace + indexTemp);
                    String ruleType = properties.getProperty(tableNameTemplateReplace + ruleTemplateReplace + ruleTypeTemp);
                    String modValue = properties.getProperty(tableNameTemplateReplace + ruleTemplateReplace + modValueTemp);
                    String addValue = properties.getProperty(tableNameTemplateReplace + ruleTemplateReplace + addValueTemp);
                    String dbRule = properties.getProperty(tableNameTemplateReplace + ruleTemplateReplace + dbRuleTemp);
                    if (StringUtils.isNotBlank(columnName)) {
                        DbConfig.setTableDbRule(tableName, dbRule);
                        Rule ruleObj = new Rule().setColumnName(columnName).setRule(rule).setIndex(index).setRuleType(ruleType);
                        if (StringUtils.isNotBlank(ruleType)) {
                            if (StringUtils.isBlank(modValue)) {
                                throw ParameterException.throwException("分表参数错误，modValue不能为空");
                            } else {
                                ruleObj.setModValue(Integer.parseInt(modValue));
                            }
                            if (StringUtils.isNotBlank(addValue)) {
                                ruleObj.setAddValue(Integer.parseInt(addValue));
                            }
                        }
                        ruleList.add(ruleObj);
                    } else {
                        break;
                    }
                    ++ruleIndex;
                }
                TableRule tableRule = new TableRule().setTableName(tableName).setRuleList(ruleList);
                tableRuleMap.put(tableName, tableRule);
            } else {
                break;
            }
            ++tableNameIndex;
        }
    }

    public Map<String, TableRule> getTableRuleMap() {
        return tableRuleMap;
    }

    public ScriptEngine getEngine() {
        return engine;
    }

    public ThreadPoolTaskExecutor getSplitTableExecutor() {
        return splitTableExecutor;
    }
}
