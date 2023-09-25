package org.acme.example;

public class TodoException extends RuntimeException {

	public TodoException() {
	}

	public TodoException(Throwable cause) {
		super(cause);
	}

	public TodoException(String message) {
		super(message);
	}

	public TodoException(String message, Throwable cause) {
		super(message, cause);
	}
}