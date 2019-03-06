package com.liang.dao.jdbc.split.service;

import com.liang.dao.jdbc.split.db.DBIndexHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * Created by liangzhiyan on 2017/6/21.
 */
@Service
public class CreateTable {

    private static final Logger LOG = LoggerFactory.getLogger(CreateTable.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void createIdTable() {
        LOG.info("begin create bm_split_table_id table");
        String createSql = "CREATE TABLE IF NOT EXISTS `bm_split_table_id` (\n" +
                "  `id` bigint(20) NOT NULL AUTO_INCREMENT,\n" +
                "  `create_time` int(11) NOT NULL DEFAULT '0' COMMENT '创建时间',\n" +
                "  PRIMARY KEY (`id`)\n" +
                ") ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;";
        jdbcTemplate.execute(createSql);
        LOG.info("end create bm_split_table_id table");
    }

    public void createSplitTable(String table, Set<Integer> idxSet) {
        String oldTable = table;
        if (table.endsWith("-") || table.endsWith("_")) {
            oldTable = oldTable.substring(0, oldTable.length() - 1);
        }
        for (int idx : idxSet) {
            LOG.info("begin create " + table + idx + " table");
            String createSql = "CREATE TABLE IF NOT EXISTS " + table + idx + " LIKE " + oldTable;
            DBIndexHelper.setDataSource(table, idx);
            jdbcTemplate.execute(createSql);
            LOG.info("end create " + table + idx + " table");
        }
        DBIndexHelper.clean();
    }

    public void createSqlExeFailTable() {
        LOG.info("begin create sql_exe_fail table");
        String createSql = "CREATE TABLE IF NOT EXISTS `sql_exe_fail` (\n" +
                "  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,\n" +
                "  `sql` varchar(4096) NOT NULL DEFAULT '' COMMENT '执行失败的sql对象',\n" +
                "  `status` int(2) NOT NULL COMMENT '状态（初始状态0，1重新执行成功，4重新执行失败）',\n" +
                "  `create_time` int(11) NOT NULL COMMENT '创建时间',\n" +
                "  `update_time` int(11) NOT NULL COMMENT '更新时间',\n" +
                "  PRIMARY KEY (`id`)\n" +
                ") ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4 COMMENT='sql执行失败表';";
        jdbcTemplate.execute(createSql);
        LOG.info("end create sql_exe_fail table");
    }

    public void createForbiddenTable() {
        LOG.info("begin create forbiden table");
        String createSql = "CREATE TABLE IF NOT EXISTS `forbidden` (\n" +
                "  `id` bigint(20) NOT NULL AUTO_INCREMENT,\n" +
                "  `controller_type` int(11) NOT NULL COMMENT '流控类型',\n" +
                "  `controller_begin_time` bigint(20) NOT NULL COMMENT '流控开始时间',\n" +
                "  `controller_time` bigint(20) NOT NULL COMMENT '流控时间',\n" +
                "  `value` varchar(128) NOT NULL COMMENT '需要流控的对象',\n" +
                "  `uri` varchar(128) NOT NULL COMMENT '需要控制的uri地址',\n" +
                "  `rate` int(11) NOT NULL DEFAULT '100' COMMENT '控制比率',\n" +
                "  `open` bit(1) NOT NULL DEFAULT b'1' COMMENT '是否打开',\n" +
                "  `forever_controller` bit(1) NOT NULL DEFAULT b'1' COMMENT '是否永久开启流控',\n" +
                "  `qps` bigint(20) NOT NULL COMMENT 'qps',\n" +
                "  `same_time_q` int(11) NOT NULL COMMENT '并发访问量',\n" +
                "  PRIMARY KEY (`id`)\n" +
                ") ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;";
        jdbcTemplate.execute(createSql);
        LOG.info("end create forbiden table");
    }

}
