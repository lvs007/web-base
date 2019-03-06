package com.liang.common.http.api.exception;

/**
 * HTTP连接过程中的异常，可以用来封装IOException
 *
 */
public class HttpException extends FallbackException {
    public HttpException() {
    }

    public HttpException(String detailMessage) {
        super(detailMessage);
    }

    public HttpException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public HttpException(Throwable throwable) {
        super(throwable);
    }
}
