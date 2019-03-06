package com.liang.dao.jdbc.split.service;

import com.liang.dao.jdbc.common.SqlPath;
import com.liang.dao.jdbc.split.dao.SplitTableIdDaoImpl;
import com.liang.dao.jdbc.split.entity.BmSplitTableId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * Created by liangzhiyan on 2017/6/21.
 */
@Service
public class SplitTableSomeDbIdGenerate {

    private static final Logger LOG = LoggerFactory.getLogger(SplitTableSomeDbIdGenerate.class);

    @Autowired
    private SplitTableIdDaoImpl splitTableIdDao;

    public long getId() {
        BmSplitTableId bmSplitTableId = BmSplitTableId.build();
        splitTableIdDao.insert(bmSplitTableId);
        return bmSplitTableId.getId();
    }

    public void deleteData() {
        LOG.info("bm_split_table_id开始清除无用数据");
        BmSplitTableId bmSplitTableId = splitTableIdDao.findOne(SqlPath.orderBy().desc("id").limit(1));
        long id = bmSplitTableId.getId() - 1;
        splitTableIdDao.deleteByLtId(id);
        LOG.info("bm_split_table_id清除无用数据结束，最后清除的ID：{}",id);
    }
}
