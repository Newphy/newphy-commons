package cn.newphy.data.exception;

public class DataException extends RuntimeException {

	private static final long serialVersionUID = -568589479355852797L;

	public DataException() {
		super();
	}

	public DataException(String message, Throwable cause) {
		super(message, cause);
	}

	public DataException(String message) {
		super(message);
	}

	public DataException(Throwable cause) {
		super(cause);
	}

}
