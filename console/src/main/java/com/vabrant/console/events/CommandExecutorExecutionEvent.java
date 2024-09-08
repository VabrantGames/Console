
package com.vabrant.console.events;

public class CommandExecutorExecutionEvent extends DefaultEvent {

	private boolean success;
	private String command;
	private String errorMessage;

	public CommandExecutorExecutionEvent setCommand (String command) {
		this.command = command;
		return this;
	}

	public String getCommand () {
		return command;
	}

	public CommandExecutorExecutionEvent setErrorMessage (String errorMessage) {
		this.errorMessage = errorMessage;
		return this;
	}

	public String getErrorMessage () {
		return errorMessage;
	}

	public CommandExecutorExecutionEvent setExecutionResult (boolean success) {
		this.success = success;
		return this;
	}

	public boolean getExecutionResult () {
		return success;
	}

	public void clear () {
		success = false;
		command = null;
		errorMessage = null;
	}
}
