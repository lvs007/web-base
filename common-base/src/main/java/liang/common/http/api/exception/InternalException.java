package liang.common.http.api.exception;

/**
 * 内部错误的异常
 * 比如空指针或者处理不当造成的数据问题
 *
 */
public class InternalException extends FallbackException {
    public InternalException() {
    }

    public InternalException(String detailMessage) {
        super(detailMessage);
    }

    public InternalException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public InternalException(Throwable throwable) {
        super(throwable);
    }
}
