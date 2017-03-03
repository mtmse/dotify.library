package org.daisy.dotify.common.split;

public class ReuseNotSupportedException extends RuntimeException {

	public ReuseNotSupportedException() {
		super();
	}

	public ReuseNotSupportedException(String message) {
		super(message);
	}

	public ReuseNotSupportedException(Throwable cause) {
		super(cause);
	}

	public ReuseNotSupportedException(String message, Throwable cause) {
		super(message, cause);
	}

	public ReuseNotSupportedException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
