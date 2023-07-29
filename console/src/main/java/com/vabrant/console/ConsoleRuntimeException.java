
package com.vabrant.console;

public class ConsoleRuntimeException extends RuntimeException {

	public ConsoleRuntimeException (String message) {
		super(message);
	}

	public ConsoleRuntimeException (Throwable throwable) {
		super(throwable);
	}

	public ConsoleRuntimeException (String message, Throwable throwable) {
		super(message, throwable);
	}
}
