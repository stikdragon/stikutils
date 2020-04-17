package uk.co.stikman.table;

public class DataTableException extends RuntimeException {

	/**
	 * 
	 */
	private static final long	serialVersionUID	= -7677255465120192279L;

	public DataTableException() {
	}

	public DataTableException(String message) {
		super(message);
	}

	public DataTableException(Throwable cause) {
		super(cause);
	}

	public DataTableException(String message, Throwable cause) {
		super(message, cause);
	}

}
