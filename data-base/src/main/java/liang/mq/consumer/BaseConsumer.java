package liang.mq.consumer;

import com.alibaba.fastjson.JSON;
import liang.common.valid.ParameterValidate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * Created by mc-050 on 2016/9/14.
 */
public abstract class BaseConsumer {

    protected static final Logger LOG = LoggerFactory.getLogger(BaseConsumer.class);

    private volatile boolean cycle = true;

    private MnsService mnsService;

    private ExecutorService executorService;

    @PostConstruct
    public void init() {
        ParameterValidate.assertNull(mnsService);
        try {
            LOG.info("[BaseConsumer.init]消息队列:{},消费线程开始启动。", mnsService.getTopicName());
            mnsService.init();
            executorService = Executors.newSingleThreadExecutor(new ThreadFactory() {
                @Override
                public Thread newThread(Runnable r) {
                    return new Thread(r, mnsService.getTopicName() + "_thread");
                }
            });
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    while (cycle) {
                        try {
                            consumerMessage(pop());
                        } catch (Exception e) {
                            LOG.error("consumer message has error !", e);
                        }
                    }
                }
            });
            LOG.info("[BaseConsumer.init]消息队列:{},消费线程启动结束", mnsService.getTopicName());
        } catch (Exception e) {
            LOG.error("启动消费线程出错！", e);
        }
    }

    private String pop() {
        return mnsService.pop();
    }

    public <T> boolean sendMsg(T entity) {
        ParameterValidate.assertNull(entity);
        try {
            Message message = new Message(JSON.toJSONString(entity), entity.getClass().getName());
            return mnsService.sendMsg(message);
        } catch (Exception e) {
            LOG.error("[sendMsg]发送消息时出错！", e);
        }
        return false;
    }

    protected Message parseJsonToMessage(String json) {
        Message message = null;
        try {
            message = JSON.parseObject(json, Message.class);
        } catch (Exception e) {
            LOG.error("[parseJsonToMessage]转换消息格式出错！json = " + json, e);
        }
        return message;
    }

    public static class Message {
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

        @Override
        public String toString() {
            return JSON.toJSONString(this);
        }

        public byte[] toByteArray() {
            return JSON.toJSONString(this).getBytes();
        }
    }

    public void setMnsService(MnsService mnsService) {
        this.mnsService = mnsService;
    }

    @PreDestroy
    public void destroy() {
        cycle = false;
        mnsService.destroy();
        executorService.shutdown();
    }

    protected abstract void consumerMessage(String json);
}
