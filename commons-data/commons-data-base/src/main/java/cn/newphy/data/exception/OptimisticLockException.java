package cn.newphy.data.exception;

public class OptimisticLockException extends DataException {
	private static final long serialVersionUID = 1939860986587799803L;

	public OptimisticLockException() {
		super();
	}

	public OptimisticLockException(String message, Throwable cause) {
		super(message, cause);
	}

	public OptimisticLockException(String message) {
		super(message);
	}

	public OptimisticLockException(Throwable cause) {
		super(cause);
	}

}
