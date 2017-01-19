package liang.mvc.exception;

/**
 * @author liangzhiyan
 */
public class ServiceException extends RuntimeException {

    private static final long serialVersionUID = -3402799698277825918L;

    public ServiceException(String message) {
        super(message);
    }

    public ServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public ServiceException(Throwable cause) {
        super(cause);
    }

}
