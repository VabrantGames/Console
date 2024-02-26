
package com.vabrant.console;

public class ConsoleExtensionExecutable {

	protected ConsoleExtension extension;
	protected Object[] argument;

	public ConsoleExtensionExecutable() {
		this(null);
	}

	public ConsoleExtensionExecutable (ConsoleExtension extension) {
		setConsoleExtension(extension);
	}

	public void setConsoleExtension (ConsoleExtension extension) {
		this.extension = extension;
	}

	public ConsoleExtension getConsoleExtension () {
		return extension;
	}

	public void setArguments (Object... argument) {
		this.argument = argument;
	}

	public Object[] getArguments () {
		return argument;
	}
}
