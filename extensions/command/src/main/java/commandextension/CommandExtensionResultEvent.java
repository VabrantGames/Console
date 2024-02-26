
package commandextension;

import com.vabrant.console.events.DefaultEvent;

public class CommandExtensionResultEvent extends DefaultEvent {

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
}
