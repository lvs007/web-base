package com.liang.common.exception;

/**
 * Created by mc-050 on 2017/2/13 10:50.
 * KIVEN will tell you life,send email to xxx@163.com
 */
public class BaseException extends RuntimeException {

    private String message;
    private int code;

    public BaseException(String message) {
        super(message);
        this.message = message;
    }

    public BaseException(String message, int code) {
        super(message);
        this.message = message;
        this.code = code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
