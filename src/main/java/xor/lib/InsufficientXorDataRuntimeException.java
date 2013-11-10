package xor.lib;

public class InsufficientXorDataRuntimeException extends RuntimeException {

	private static final long serialVersionUID = 422545934941395513L;

	public InsufficientXorDataRuntimeException() {
		super();
	}

	public InsufficientXorDataRuntimeException(String message, Throwable cause) {
		super(message, cause);
	}

	public InsufficientXorDataRuntimeException(String message) {
		super(message);
	}

	public InsufficientXorDataRuntimeException(Throwable cause) {
		super(cause);
	}

}
