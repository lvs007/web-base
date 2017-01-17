package liang.dao;

import org.springframework.jdbc.core.JdbcTemplate;

import javax.annotation.Resource;

/**
 * Created by mc-050 on 2017/1/16 11:07.
 * KIVEN will tell you life,send email to xxx@163.com
 */
public abstract class AbstractDao<E> {

    @Resource
    private JdbcTemplate jdbcTemplate;

    public E findOne(long id){
        return null;
    }
}
