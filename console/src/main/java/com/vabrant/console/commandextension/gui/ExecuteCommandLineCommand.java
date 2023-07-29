
package com.vabrant.console.commandextension.gui;

import com.vabrant.console.commandextension.CommandData;
import com.vabrant.console.gui.shortcuts.ShortcutCommand;
import com.vabrant.console.gui.DefaultView;

public class ExecuteCommandLineCommand implements ShortcutCommand {

	private final DefaultView<?> view;
	private final CommandLinePanel panel;
	private final CommandData data;

	public ExecuteCommandLineCommand (CommandData data, DefaultView<?> view, CommandLinePanel panel) {
		this.data = data;
		this.view = view;
		this.panel = panel;
	}

	@Override
	public void execute () {
		if (view.isHidden()) return;
		data.getConsoleStrategy().execute(panel.getText());
	}
}
