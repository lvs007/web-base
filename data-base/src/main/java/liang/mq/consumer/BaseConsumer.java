package liang.mq.consumer;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * Created by mc-050 on 2016/9/14.
 */
public abstract class BaseConsumer {

    private boolean cycle = true;
    private Thread thread;

    private String accessId;
    private String accessKey;
    private String endPoint;
    private String queueName;

    @PostConstruct
    public void init() {
        try {

            thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (cycle) {
                        try {
                            consumerMessage(pop());
                        } catch (Exception e) {
                        }
                    }
                }
            });
            thread.setName(queueName + "_thread");
            thread.setDaemon(true);
            thread.start();
        } catch (Exception e) {
        }
    }

    private String pop() {
        return "";
    }

    public <T> boolean sendMsg(T entity) {
        try {
            return true;
        } catch (Exception e) {
        }
        return false;
    }

    @PreDestroy
    public void destroy() {
        cycle = false;
    }

    public void setAccessId(String accessId) {
        this.accessId = accessId;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public void setEndPoint(String endPoint) {
        this.endPoint = endPoint;
    }

    public void setQueueName(String queueName) {
        this.queueName = queueName;
    }

    protected abstract void consumerMessage(String json);
}
