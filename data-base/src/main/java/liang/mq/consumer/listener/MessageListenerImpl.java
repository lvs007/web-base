package liang.mq.consumer.listener;

import org.springframework.stereotype.Service;

/**
 * Created by mc-050 on 2016/11/1.
 */
@Service
public class MessageListenerImpl implements MessageListener {


    @Override
    public <T> void doTransferMNSMessage(T t) {

    }
}
