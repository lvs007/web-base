package liang.mq.consumer;

import liang.mq.consumer.listener.MessageListener;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by mc-050 on 2016/9/14.
 */
public class MessageConsumer extends BaseConsumer {

    private MessageListener messageListener;

    @Override
    protected void consumerMessage(String json) {
        messageListener.doTransferMNSMessage(null);
    }

    @Autowired
    public void setMessageListener(MessageListener messageListener) {
        this.messageListener = messageListener;
    }
}
