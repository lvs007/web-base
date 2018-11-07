package liang.email;

import java.util.Set;

/**
 * Created by liangzhiyan on 2017/5/27.
 */
public interface SendEmail {

    //简单的发送方式
    void sendEmail(String from, String to, String title, String content);
    void sendEmail(String from, Set<String> to, String title, String content);
    //

}
