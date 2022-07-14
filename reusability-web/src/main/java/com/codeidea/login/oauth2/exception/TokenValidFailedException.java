package com.codeidea.login.oauth2.exception;

public class TokenValidFailedException extends RuntimeException {

	private static final long serialVersionUID = -2292656330484099114L;

	public TokenValidFailedException() {
		super("Failed to generate Token.");
	}

	@SuppressWarnings("unused")
	private TokenValidFailedException(String message) {
		super(message);
	}
}
