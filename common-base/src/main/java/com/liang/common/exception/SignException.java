package com.liang.common.exception;

/**
 * Created by mc-050 on 2017/2/14 9:54.
 * KIVEN will tell you life,send email to xxx@163.com
 */
public class SignException extends BaseException{
    public SignException(String message) {
        super(message);
    }

    public static SignException throwException(String message){
        return new SignException(message);
    }
}
