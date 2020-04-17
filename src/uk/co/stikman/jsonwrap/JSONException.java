package uk.co.stikman.jsonwrap;

public class JSONException extends RuntimeException {

	private static final long	serialVersionUID	= -7152871559198693099L;

	public JSONException() {
	}

	public JSONException(String message) {
		super(message);
	}

	public JSONException(Throwable cause) {
		super(cause);
	}

	public JSONException(String message, Throwable cause) {
		super(message, cause);
	}

	public JSONException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
