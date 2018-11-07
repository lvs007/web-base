package liang.common.http.api.exception;


import liang.common.http.api.ApiResponse;

/**
 * API操作的异常类，一般用于写操作，读操作不会抛出此异常
 *
 */
public class ApiException extends Exception {
    private int errorCode;
    private String message;
    private ApiResponse apiResponse;

    public ApiException(ApiResponse apiResponse) {
        this(apiResponse.getErrorCode(), apiResponse.getMessage());
        this.apiResponse = apiResponse;
    }

    @Deprecated
    public ApiException(int errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
        this.message = message;
    }

    public ApiResponse getApiResponse() {
        return apiResponse;
    }

    public int getErrorCode() {
        return errorCode;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
