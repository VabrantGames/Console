
package com.vabrant.console.commandextension;

public class CommandEvent {

	private String command;
	private String errorMessage;
	private CommandData data;

	public CommandEvent (CommandData data) {
		this.data = data;
	}

	public CommandEvent setCommand (String command) {
		this.command = command;
		return this;
	}

	public CommandData getData () {
		return data;
	}

	public String getCommand () {
		return command;
	}

	public CommandEvent setErrorMessage (String errorMessage) {
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
