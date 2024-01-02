package com.vabrant.console;

public class ConsoleExtensionExecutable {

	protected ConsoleExtension extension;
	protected Object argument;

	public void setConsoleExtension (ConsoleExtension extension) {
		this.extension = extension;
	}

	public ConsoleExtension getConsoleExtension() {
		return extension;
	}

	public void setArgument (Object argument) {
		this.argument = argument;
	}

	public Object getArgument() {
		return argument;
	}
}
