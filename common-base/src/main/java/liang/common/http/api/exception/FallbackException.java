package liang.common.http.api.exception;

/**
 *
 */
public class FallbackException extends Exception {
    public FallbackException() {
    }

    public FallbackException(String detailMessage) {
        super(detailMessage);
    }

    public FallbackException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public FallbackException(Throwable throwable) {
        super(throwable);
    }
}
