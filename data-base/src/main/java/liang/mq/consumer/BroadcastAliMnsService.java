package liang.mq.consumer;

import com.alibaba.fastjson.JSON;
import com.aliyun.mns.client.CloudAccount;
import com.aliyun.mns.client.CloudTopic;
import com.aliyun.mns.client.MNSClient;
import com.aliyun.mns.model.Base64TopicMessage;
import com.aliyun.mns.model.SubscriptionMeta;
import com.aliyun.mns.model.TopicMessage;
import com.aliyun.mns.sample.HttpEndpoint;

/**
 * Created by liangzhiyan on 2017/4/27.
 */
public class BroadcastAliMnsService implements MnsService {

    private static final int wait_time = 5;
    private static final String SUBFIX = "sub-";

    private String accessId;
    private String accessKey;
    private String endPoint;
    private String topicName;

    private CloudTopic cloudTopic;

    @Override
    public void init() {
        CloudAccount account = new CloudAccount(accessId, accessKey, endPoint);
        MNSClient client = account.getMNSClient(); // 在程序中，CloudAccount以及MNSClient单例实现即可，多线程安全
        cloudTopic = client.getTopicRef(topicName);
        SubscriptionMeta subMeta = new SubscriptionMeta();
        subMeta.setSubscriptionName(SUBFIX + topicName);
        subMeta.setEndpoint(HttpEndpoint.GenEndpointLocal());
        subMeta.setNotifyContentFormat(SubscriptionMeta.NotifyContentFormat.SIMPLIFIED);
        //subMeta.setFilterTag("filterTag"); //设置订阅的filterTag
        String subUrl = cloudTopic.subscribe(subMeta);
        System.out.println("sub url = " + subUrl);
    }

    @Override
    public String pop() {

        return null;
    }

    @Override
    public <T> boolean sendMsg(T entity) {
        TopicMessage message = new Base64TopicMessage();
        message.setMessageBody(JSON.toJSONString(entity));
        cloudTopic.publishMessage(message);
        return true;
    }

    public String getTopicName() {
        return topicName;
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

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

}
