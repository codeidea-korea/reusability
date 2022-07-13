package com.codeidea.exception;

public class CustomException extends RuntimeException {

	static final long serialVersionUID = 1L;

	public CustomException() {
		super();
	}

	public CustomException(String message) {
		super(message);
	}

	public CustomException(Throwable cause) {
		super(cause);
	}

	public CustomException(String message, Throwable cause) {
		super(message, cause);
	}
}
