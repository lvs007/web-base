package liang.mq.consumer.impl;

import com.aliyun.openservices.ons.api.*;
import liang.common.valid.ParameterValidate;
import liang.mq.consumer.BaseConsumer;
import liang.mq.consumer.MnsService;
import org.apache.commons.lang3.StringUtils;

import java.util.Properties;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by liangzhiyan on 2017/5/22.
 */
public class AliOnsService implements MnsService, MessageListener {

    private String accessId;
    private String accessKey;
    private String producerId;
    private String consumerId;
    private String sendTimeOut = "3000";
    private String topicName;
    private String tag = "tag_1";
    private boolean subscribe = false;

    private boolean continues = true;

    private Producer producer;

    @Override
    public void init() {
        ParameterValidate.assertBlank(accessId);
        ParameterValidate.assertBlank(accessKey);
        ParameterValidate.assertBlank(producerId);
        ParameterValidate.assertBlank(topicName);
        Properties properties = new Properties();
        properties.put(PropertyKeyConst.ProducerId, producerId);
        properties.put(PropertyKeyConst.AccessKey, accessId);
        properties.put(PropertyKeyConst.SecretKey, accessKey);
        properties.setProperty(PropertyKeyConst.SendMsgTimeoutMillis, sendTimeOut);//设置发送超时时间，单位毫秒
        producer = ONSFactory.createProducer(properties);
        // 在发送消息前，必须调用start方法来启动Producer，只需调用一次即可。
        producer.start();
        if (subscribe) {
            subscribe();
        }
    }

    @Override
    public String pop() {
        try {
            return blockingQueue.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean sendMsg(BaseConsumer.Message entity) {
        Message msg = new Message(topicName, //你申请的TopicName
                tag, entity.toByteArray());
        producer.send(msg);
        return true;
    }

    @Override
    public String getTopicName() {
        return topicName;
    }

    @Override
    public void destroy() {
        continues = false;
    }

    /**
     * 订阅消息
     */
    public void subscribe() {
        Properties properties = new Properties();
        properties.put(PropertyKeyConst.MessageModel, PropertyValueConst.BROADCASTING);//采用广播消费模式
        properties.put(PropertyKeyConst.ConsumerId, consumerId);
        properties.put(PropertyKeyConst.AccessKey, accessId);
        properties.put(PropertyKeyConst.SecretKey, accessKey);
        Consumer consumer = ONSFactory.createConsumer(properties);
        consumer.subscribe(topicName, tag, this);
        consumer.start();
    }

    @Override
    public Action consume(Message message, ConsumeContext context) {
        if (!continues) {
            return Action.ReconsumeLater;
        } else {
            if (StringUtils.equals(message.getTopic(), topicName)
                    && StringUtils.equals(message.getTag(), tag)) {
                String body = new String(message.getBody());
                if (blockingQueue.size() == 0) {
                    blockingQueue.add(body);
                }
            }
            return Action.CommitMessage;
        }
    }

    private final BlockingQueue<String> blockingQueue = new LinkedBlockingQueue<>();

    public void setAccessId(String accessId) {
        this.accessId = accessId;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public void setProducerId(String producerId) {
        this.producerId = producerId;
    }

    public void setConsumerId(String consumerId) {
        this.consumerId = consumerId;
    }

    public void setSendTimeOut(String sendTimeOut) {
        this.sendTimeOut = sendTimeOut;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public void setSubscribe(boolean subscribe) {
        this.subscribe = subscribe;
    }
}
