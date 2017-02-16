package liang.mq.consumer;

import com.alibaba.fastjson.JSON;
import liang.common.valid.ParameterValidate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * Created by mc-050 on 2016/9/14.
 */
public abstract class BaseConsumer {

    protected static final Logger LOG = LoggerFactory.getLogger(BaseConsumer.class);

    private boolean cycle = true;
    private Thread thread;

    private String accessId;
    private String accessKey;
    private String endPoint;
    private String queueName;

    @PostConstruct
    public void init() {
        try {
            LOG.info("[BaseConsumer.init]消息队列消费线程开始启动。");
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
            LOG.info("[BaseConsumer.init]消息队列消费线程启动结束");
        } catch (Exception e) {
        }
    }

    private String pop() {
        //todo:这里实现获取消息队列内容的代码。。。

        return "";
    }

    public <T> boolean sendMsg(T entity) {
        ParameterValidate.assertNull(entity);
        try {
            Message message = new Message(JSON.toJSONString(entity), entity.getClass().getName());
            //todo:这里实现mq传送消息的代码。。。

            return true;
        } catch (Exception e) {
            LOG.error("[sendMsg]发送消息时出错！", e);
        }
        return false;
    }

    protected class Message {
        public Message() {
        }

        public Message(String json, String messageType) {
            this.json = json;
            this.messageType = messageType;
        }

        private String json;

        private String messageType;

        public String getJson() {
            return json;
        }

        public void setJson(String json) {
            this.json = json;
        }

        public String getMessageType() {
            return messageType;
        }

        public void setMessageType(String messageType) {
            this.messageType = messageType;
        }
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
