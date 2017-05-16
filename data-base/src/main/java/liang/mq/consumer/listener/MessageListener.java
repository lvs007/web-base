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
    String getType();

    /**
     * 对返回的消息进行转换，转换成指定的消息对象。
     *
     * @param json
     * @return
     */
    T parseJson(String json);

    /**
     * 对消息对象进行处理
     *
     * @param t
     */
    void consumerMessage(T t);
}
