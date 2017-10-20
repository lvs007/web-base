/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package liang.dao.jdbc.common;

import org.apache.commons.collections4.CollectionUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * 此类只做统计，不提供和数据库相关的任务服务
 *
 * @author
 */
public class StorageManager {

    private static final Logger LOG = LoggerFactory.getLogger(StorageManager.class);
    /**
     * 批量插入的最大一次插入数量，如果超过这个数量，则会分批进行插入
     */
    protected int insertBatchMaxSize = 10000;
    /**
     * 是否被禁用,只有在数据库访问不畅的情况下才会被禁用
     * ，被禁用以后，还是有可能再启动的
     */
    private boolean disabled;
    /**
     * 每次清理状态的相隔时间，单位为秒，时间一到，则所有的状态都清空
     * 默认是一分钟清空一次
     */
    private int resetInterval = 60;
    /**
     * 最多出错次数，超过多少次出错以后，数据库就被禁用
     * 默认6000次
     */
    private int maxErrorCount = 6000;
    /**
     * 最多查执行的次数，超过多少次以后，数据库就被禁用
     */
    private int maxSlowCount = 1000;
    /**
     * 最小的慢时间，也就是超过多少时间算是慢执行，单位为毫秒
     * 默认是1秒
     */
    private long minSlowTime = 1000;
    /**
     * 统计对象，在到了清理时间的时候，会把此对象打到日志里面
     */
    private Stat stat = new Stat();
    /**
     * 为了方便分区统计，可以给每个区取个名字，
     */
    private String name;

    /**
     * 添加一次错误执行的记录
     *
     * @param line
     */
    void addErrorCount(SqlLine line) {
        resetIfNeed();
        stat.errorCount++;
        stat.executeCount++;
        stat.executeTime += line.usedTime;
        stat.errorSqlList.add(line);
        if (stat.errorCount > maxErrorCount) {
            disabled = true;
        }
    }

    /**
     * 添加一次执行的记录
     *
     * @param line
     */
    void addExecuteCount(SqlLine line) {
        resetIfNeed();
        if (line.usedTime > minSlowTime) {
            stat.slowCount++;
            stat.slowSqlList.add(line);
            if (stat.slowCount > maxSlowCount) {
                disabled = true;
            }
        }
        stat.executeCount++;
        stat.executeTime += line.usedTime;
    }

    /**
     * 检查是否需要重置
     */
    private void resetIfNeed() {
        if (System.currentTimeMillis() - stat.lastResetTime > resetInterval * 1000L) {
            //先把内容根据不同的情况输出的日志中
            Stat local = stat;
            stat = new Stat();
            doLog(local);
            disabled = false;
        }
    }

    private void doLog(final Stat stat) {
        DecimalFormat df = new DecimalFormat("0.00");
        LOG.info("【{}】:errorCount={},slowCount={},executeCount={},allTime={}ms,avgTime={}ms,blockCount={}",
                name,
                stat.errorCount,
                stat.slowCount,
                stat.executeCount,
                stat.executeTime,
                df.format(stat.executeTime * 1.f / stat.executeCount),
                stat.blockCount);
        if (CollectionUtils.isNotEmpty(stat.errorSqlList) || CollectionUtils.isNotEmpty(stat.slowSqlList)) {
            LOG.error("=====================================================================================================");
            for (SqlLine line : stat.errorSqlList) {
                LOG.error("[ERROR]: {} - {} - {} - {}", new DateTime(line.createTime).toString("yyyy-MM-dd HH:mm:ss.SSSS"), line.usedTime, line.sql, line.errorInfo);
            }
            for (SqlLine line : stat.slowSqlList) {
                LOG.error("[SLOW]: {} - {} - {}", new DateTime(line.createTime).toString("yyyy-MM-dd HH:mm:ss.SSSS"), line.usedTime, line.sql);
            }
        }
    }

    /**
     * 当前的数据源是否可用
     *
     * @return
     */
    public boolean isAvailable() {
        resetIfNeed();
        if (disabled) {
            stat.blockCount++;
            return false;
        }
        return true;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public int getResetInterval() {
        return resetInterval;
    }

    public void setResetInterval(int resetInterval) {
        this.resetInterval = resetInterval;
    }

    public int getMaxErrorCount() {
        return maxErrorCount;
    }

    public void setMaxErrorCount(int maxErrorCount) {
        this.maxErrorCount = maxErrorCount;
    }

    public int getMaxSlowCount() {
        return maxSlowCount;
    }

    public void setMaxSlowCount(int maxSlowCount) {
        this.maxSlowCount = maxSlowCount;
    }

    public long getMinSlowTime() {
        return minSlowTime;
    }

    public void setMinSlowTime(long minSlowTime) {
        this.minSlowTime = minSlowTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getInsertBatchMaxSize() {
        return insertBatchMaxSize;
    }

    public void setInsertBatchMaxSize(int insertBatchMaxSize) {
        this.insertBatchMaxSize = insertBatchMaxSize;
    }

    /**
     * 一个小的统计对象，本来是想用Atom之类的，但是想到并发并没有
     * 那么高，所以就直接用了int，long之类的
     */
    static class Stat {

        long lastResetTime = System.currentTimeMillis();//上次重置的时间
        int errorCount;//出错次数
        int slowCount;//慢执行次数
        int executeCount;//执行总次数
        long executeTime;//执行总耗时
        List<SqlLine> slowSqlList = new ArrayList<>();//很慢执行的sql语句
        List<SqlLine> errorSqlList = new ArrayList<>();//出错的SQL语句
        int blockCount;//因为数据库被禁用，block的调用次数
    }

    static class SqlLine {

        String errorInfo;//错误信息，如果有的话
        String sql;//执行的sql
        long createTime = System.currentTimeMillis();//创建的时间
        long usedTime;//使用的时间
    }
}
