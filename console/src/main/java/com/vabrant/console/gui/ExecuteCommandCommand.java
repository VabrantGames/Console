
package com.vabrant.console.gui;

import com.vabrant.console.ConsoleCommand;

public class ExecuteCommandCommand implements ConsoleCommand {

	private final GUIConsole console;

	public ExecuteCommandCommand (GUIConsole console) {
		this.console = console;
	}

	@Override
	public void execute () {
		if (console.isHidden()) return;
		CommandLine cl = console.getCommandLine();

// console.execute(console.getCommandExecutionStrategy(), cl.getText());
// try {j
		console.getCommandExecutionStrategy().execute(cl.getText());
// } catch (Exception e) {
// e.printStackTrace();
// }
// console.execute(cl.getText());
// cl.clearCommandLine();
	}
}
