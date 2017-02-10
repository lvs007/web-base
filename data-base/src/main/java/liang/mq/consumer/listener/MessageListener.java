package liang.mq.consumer.listener;


/**
 * Created by mc-050 on 2016/11/1.
 */
public interface MessageListener {
    <T> void doTransferMNSMessage(T t);
}
