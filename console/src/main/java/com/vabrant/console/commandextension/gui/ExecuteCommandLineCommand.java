
package com.vabrant.console.commandextension.gui;

import com.vabrant.console.commandextension.CommandData;
import com.vabrant.console.gui.views.ViewManager;

@Deprecated
public class ExecuteCommandLineCommand implements Runnable {

	private final ViewManager view;
	private final CommandLinePanel panel;
	private final CommandData data;

	public ExecuteCommandLineCommand (CommandData data, ViewManager view, CommandLinePanel panel) {
		this.data = data;
		this.view = view;
		this.panel = panel;
	}

	@Override
	public void run () {
		if (view.isHidden()) return;
// data.getConsoleStrategy().execute(panel.getText());
	}
}
