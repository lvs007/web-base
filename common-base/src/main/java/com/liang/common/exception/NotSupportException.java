package com.liang.common.exception;

/**
 * Created by mc-050 on 2017/2/14 9:54.
 * KIVEN will tell you life,send email to xxx@163.com
 */
public class NotSupportException extends BaseException{
    public NotSupportException(String message) {
        super(message);
    }

    public static NotSupportException throwException(String message){
        return new NotSupportException(message);
    }
}
