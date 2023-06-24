
package com.vabrant.console;

public class CommandExecutionEvent {

	private String command;
	private String errorMessage;
	private CommandExecutionData data;

	public CommandExecutionEvent(CommandExecutionData data) {
		this.data = data;
	}

	public CommandExecutionEvent setCommand (String command) {
		this.command = command;
		return this;
	}

	public CommandExecutionData getData() {
		return data;
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
