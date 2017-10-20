package liang.dao.jdbc.split.db;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * Created by liangzhiyan on 2017/6/28.
 */
public class SplitDbRouter extends AbstractRoutingDataSource {

    @Override
    protected Object determineCurrentLookupKey() {
        return DBIndexHelper.getDbIndex();
    }
}
