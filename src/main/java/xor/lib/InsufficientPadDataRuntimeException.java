package xor.lib;

public class InsufficientPadDataRuntimeException extends RuntimeException {

	public InsufficientPadDataRuntimeException() {
		super();
	}

	public InsufficientPadDataRuntimeException(String message,
																						 Throwable cause) {
		super(message, cause);
	}

	public InsufficientPadDataRuntimeException(String message) {
		super(message);
	}

	public InsufficientPadDataRuntimeException(Throwable cause) {
		super(cause);
	}

}
