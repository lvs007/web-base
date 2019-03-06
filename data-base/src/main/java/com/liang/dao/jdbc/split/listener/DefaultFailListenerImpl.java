package com.liang.dao.jdbc.split.listener;

import com.alibaba.fastjson.JSON;
import com.liang.dao.jdbc.common.Sql;
import com.liang.dao.jdbc.split.dao.SqlExeFailEntityDaoImpl;
import com.liang.dao.jdbc.split.entity.SqlExeFail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by liangzhiyan on 2017/7/6.
 */
@Component(value = "defaultFailListener")
public class DefaultFailListenerImpl implements FailListener {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultFailListenerImpl.class);

    @Autowired
    private SqlExeFailEntityDaoImpl sqlExeFailEntityDao;

    @Override
    public void doSomething(Sql sql) {
        String sqlStr = JSON.toJSONString(sql);
        if (sqlStr.length() < 4096) {
            try {
                int time = (int) (System.currentTimeMillis() / 1000);
                SqlExeFail sqlExeFailEntity = new SqlExeFail();
                sqlExeFailEntity.setSql(sqlStr);
                sqlExeFailEntity.setStatus(0);
                sqlExeFailEntity.setCreateTime(time);
                sqlExeFailEntity.setUpdateTime(time);
                sqlExeFailEntityDao.insert(sqlExeFailEntity);
            } catch (Exception e) {
                LOG.error("插入sql执行失败表出错！", e);
            }
        }
        LOG.error("更新从维度数据失败！，sql: {}", sqlStr);
    }
}
