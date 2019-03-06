package com.liang.dao.jdbc.split.listener;

import com.liang.dao.jdbc.common.Sql;

/**
 * Created by liangzhiyan on 2017/7/6.
 */
public interface FailListener {
    void doSomething(Sql sql);
}
