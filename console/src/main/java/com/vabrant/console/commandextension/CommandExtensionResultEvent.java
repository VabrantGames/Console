
package com.vabrant.console.commandextension;

import com.badlogic.gdx.utils.Array;
import com.vabrant.console.events.Event;
import com.vabrant.console.events.EventListener;

public class CommandExtensionResultEvent implements Event {

	boolean success;
	private String command;
	private String errorMessage;

	public CommandExtensionResultEvent setCommand (String command) {
		this.command = command;
		return this;
	}

	public String getCommand () {
		return command;
	}

	public CommandExtensionResultEvent setErrorMessage (String errorMessage) {
		this.errorMessage = errorMessage;
		return this;
	}

	public String getErrorMessage () {
		return errorMessage;
	}

	public CommandExtensionResultEvent setExecutionResult (boolean success) {
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

	@Override
	public <T extends Event> void handle (Array<EventListener<T>> eventListeners) {

	}
}
