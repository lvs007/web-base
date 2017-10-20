package liang.dao.jdbc.split.db;

import liang.dao.jdbc.common.Sql;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by liangzhiyan on 2017/6/27.
 */
public class SplitDbJdbcTemplate extends JdbcTemplate {

    private Map<String, DataSource> dataSourceMap = new ConcurrentHashMap<>();


    public SplitDbJdbcTemplate setDataSourceMap(Map<String, DataSource> dataSourceMap) {
        this.dataSourceMap = dataSourceMap;
        return this;
    }

    public DataSource findDataSource(Sql sql) {

        return null;
    }

    @Override
    public DataSource getDataSource() {
        return super.getDataSource();
    }
}
