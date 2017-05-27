package liang.mq.consumer.impl;

import com.alibaba.fastjson.JSON;
import com.aliyun.mns.client.CloudAccount;
import com.aliyun.mns.client.CloudQueue;
import com.aliyun.mns.client.MNSClient;
import com.aliyun.mns.model.Message;
import liang.common.valid.ParameterValidate;
import liang.mq.consumer.BaseConsumer;
import liang.mq.consumer.MnsService;

/**
 * Created by liangzhiyan on 2017/4/27.
 */
public class AliMnsService implements MnsService {

    private static final int wait_time = 5;

    private String accessId;
    private String accessKey;
    private String endPoint;
    private String queueName;

    private CloudQueue queue;
    private MNSClient client;

    @Override
    public void init() {
        ParameterValidate.assertBlank(accessId);
        ParameterValidate.assertBlank(accessKey);
        ParameterValidate.assertBlank(endPoint);
        ParameterValidate.assertBlank(queueName);
        CloudAccount account = new CloudAccount(accessId, accessKey, endPoint);
        client = account.getMNSClient(); // 在程序中，CloudAccount以及MNSClient单例实现即可，多线程安全
        queue = client.getQueueRef(queueName);
    }

    @Override
    public String pop() {
        Message message = queue.popMessage(wait_time);
        if (message == null) {
            return null;
        }
        queue.deleteMessage(message.getReceiptHandle());
        return message.getMessageBody();
    }

    @Override
    public boolean sendMsg(BaseConsumer.Message entity) {
        Message message = new Message();
        message.setMessageBody(JSON.toJSONString(entity));
        queue.putMessage(message);
        return true;
    }

    public String getTopicName() {
        return queueName;
    }

    @Override
    public void destroy() {
        client.close();
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

}
