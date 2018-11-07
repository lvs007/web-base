package liang.mvc.exception;

import org.apache.commons.lang.exception.NestableException;

/**
 * @author zhangfeng
 * 
 */
public class ServiceKeyException extends NestableException {

	private static final long serialVersionUID = 670124089609118068L;

	private String[] params;

	public ServiceKeyException(String message) {
		super(message);
	}

	public ServiceKeyException(String message, String[] params) {
		super(message);
		this.params = params;
	}

	/**
	 * Constructor for InternalException.
	 */
	public ServiceKeyException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Constructor for InternalException.
	 */
	public ServiceKeyException(Throwable cause) {
		super(cause);
	}

	/**
	 * @return the params
	 */
	public String[] getParams() {
		return params;
	}

	/**
	 * @param params the params to set
	 */
	public void setParams(String[] params) {
		this.params = params;
	}

}
