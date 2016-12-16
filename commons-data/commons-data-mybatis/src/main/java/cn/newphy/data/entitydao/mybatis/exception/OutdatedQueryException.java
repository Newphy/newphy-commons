package cn.newphy.data.entitydao.mybatis.exception;

import cn.newphy.data.exception.DataException;

public class OutdatedQueryException extends DataException {
	private static final long serialVersionUID = -7243197637871158225L;

	public OutdatedQueryException() {
		super();
	}

	public OutdatedQueryException(String message, Throwable cause) {
		super(message, cause);
	}

	public OutdatedQueryException(String message) {
		super(message);
	}

	public OutdatedQueryException(Throwable cause) {
		super(cause);
	}

}
