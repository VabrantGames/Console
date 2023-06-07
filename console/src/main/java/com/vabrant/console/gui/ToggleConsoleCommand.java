
package com.vabrant.console.gui;

import com.vabrant.console.ConsoleCommand;

public class ToggleConsoleCommand implements ConsoleCommand {

	private final GUIConsole console;

	public ToggleConsoleCommand (GUIConsole console) {
		this.console = console;
	}

	@Override
	public void execute () {
		console.setHidden(!console.isHidden());
	}
}
