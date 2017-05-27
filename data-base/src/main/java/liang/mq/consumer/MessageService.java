package liang.mq.consumer;

import com.alibaba.fastjson.JSON;
import liang.common.valid.ParameterValidate;
import liang.mq.consumer.listener.MessageListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by mc-050 on 2016/9/14.
 */
public class MessageService extends BaseConsumer implements ApplicationContextAware {

    private static final Logger LOG = LoggerFactory.getLogger(MessageService.class);

    private Map<String, MessageListener> messageListenerMap = new HashMap<>();

    public void register(Class type, MessageListener messageListener) {
        ParameterValidate.assertNull(messageListener);
        ParameterValidate.assertNull(type);
        messageListenerMap.put(type.getName(), messageListener);
    }

    @Override
    protected void consumerMessage(String json) {
        Message message = parseJsonToMessage(json);
        if (message != null) {
            MessageListener messageListener = messageListenerMap.get(message.getMessageType());
            if (messageListener != null) {
                try {
                    Object obj = JSON.parseObject(message.getJson(), messageListener.getType());
                    messageListener.consumerMessage(obj);
                } catch (Exception e) {
                    LOG.error("[consumerMessage]消费消息时出错！消息队列返回的数据：" + json, e);
                }
            }
        }
    }

    public void setMessageListenerMap(Map<String, MessageListener> messageListenerMap) {
        this.messageListenerMap = messageListenerMap;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map<String, MessageListener> map = applicationContext.getBeansOfType(MessageListener.class);
        for (MessageListener value : map.values()) {
            messageListenerMap.put(value.getType().getName(), value);
        }
    }
}
