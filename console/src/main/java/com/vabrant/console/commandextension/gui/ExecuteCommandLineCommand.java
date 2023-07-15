
package com.vabrant.console.commandextension.gui;

import com.vabrant.console.commandextension.CommandData;
import com.vabrant.console.gui.shortcuts.ConsoleCommand;
import com.vabrant.console.commandextension.gui.CommandLinePanel;
import com.vabrant.console.gui.View;

public class ExecuteCommandLineCommand implements ConsoleCommand {

	private final View<?> view;
	private final CommandLinePanel panel;
	private final CommandData data;

	public ExecuteCommandLineCommand (CommandData data, View<?> view, CommandLinePanel panel) {
		this.data = data;
		this.view = view;
		this.panel = panel;
	}

	@Override
	public void execute () {
		if (view.isHidden()) return;
		data.getExecutionStrategy().execute(panel.getText());
	}
}
