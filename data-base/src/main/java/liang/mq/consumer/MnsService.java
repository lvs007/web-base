package liang.mq.consumer;

/**
 * Created by liangzhiyan on 2017/4/27.
 */
public interface MnsService {

    void init();

    String pop();

    <T> boolean sendMsg(T entity);

    String getTopicName();
}
