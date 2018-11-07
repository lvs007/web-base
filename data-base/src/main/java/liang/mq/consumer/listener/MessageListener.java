package liang.mq.consumer.listener;


/**
 * Created by mc-050 on 2016/11/1.
 */
public interface MessageListener<T> {

    /**
     * 这个必须返回发送的消息的对象类型，
     * 如：return String.class.getName(),对象类型为String;
     *
     * @return
     */
    Class getType();

    /**
     * 对消息对象进行处理
     *
     * @param t
     */
    void consumerMessage(T t);
}
