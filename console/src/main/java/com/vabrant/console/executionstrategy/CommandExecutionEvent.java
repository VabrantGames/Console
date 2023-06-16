
package com.vabrant.console.executionstrategy;

public class CommandExecutionEvent {

	private String command;
	private String errorMessage;

	public CommandExecutionEvent setCommand (String command) {
		this.command = command;
		return this;
	}

	public String getCommand () {
		return command;
	}

	public CommandExecutionEvent setErrorMessage (String errorMessage) {
		this.errorMessage = errorMessage;
		return this;
	}

	public String getErrorMessage () {
		return errorMessage;
	}

	public void clear () {
		command = null;
		errorMessage = null;
	}
}
