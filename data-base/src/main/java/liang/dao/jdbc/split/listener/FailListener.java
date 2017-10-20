package liang.dao.jdbc.split.listener;

import liang.dao.jdbc.common.Sql;

/**
 * Created by liangzhiyan on 2017/7/6.
 */
public interface FailListener {
    void doSomething(Sql sql);
}
