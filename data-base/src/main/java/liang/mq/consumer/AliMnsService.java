package liang.mq.consumer;

import com.alibaba.fastjson.JSON;
import com.aliyun.mns.client.CloudAccount;
import com.aliyun.mns.client.CloudQueue;
import com.aliyun.mns.client.MNSClient;
import com.aliyun.mns.model.Message;

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

    @Override
    public void init() {
        CloudAccount account = new CloudAccount(accessId, accessKey, endPoint);
        MNSClient client = account.getMNSClient(); // 在程序中，CloudAccount以及MNSClient单例实现即可，多线程安全
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
    public <T> boolean sendMsg(T entity) {
        Message message = new Message();
        message.setMessageBody(JSON.toJSONString(entity));
        queue.putMessage(message);
        return true;
    }

    public String getTopicName() {
        return queueName;
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
