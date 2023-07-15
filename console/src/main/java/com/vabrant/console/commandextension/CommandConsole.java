
package com.vabrant.console.commandextension;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.vabrant.console.commandextension.gui.CommandLinePanel;
import com.vabrant.console.commandextension.gui.ExecuteCommandLineCommand;
import com.vabrant.console.gui.*;
import com.vabrant.console.gui.commands.ToggleViewVisibilityCommand;
import com.vabrant.console.gui.shortcuts.Shortcut;

public class CommandConsole extends GUIConsole {

	private TableView commandLineView;
	private CommandLinePanel commandLinePanel;
	private CommandData data;

	public CommandConsole () {
		this(null);
	}

	public CommandConsole (Batch batch) {
		this(null, null, null);
	}

	public CommandConsole (Batch batch, Skin skin, CommandConsoleConfiguration settings) {
		super(batch, skin, null);

		if (settings == null) {
			settings = new CommandConsoleConfiguration();
		}

		boolean hasConsoleView = settings.createConsoleView();

		CommandSettings commandSettings = settings.getCommandExtensionSettings();
		if (commandSettings == null) {
			commandSettings = new CommandSettings();
		}

		data = new CommandData(logManager, commandSettings);
		CommandStrategy commandStrategy = new CommandStrategy();
		commandStrategy.init(data);

		commandLinePanel = new CommandLinePanel(data);
		commandLineView = new TableView("CommandLine", commandLinePanel);
		commandLineView.getRootTable().pack();
		commandLineView.setWidthPercent(settings.commandLineWidthPercent);
		commandLinePanel.getKeyMap().add(new ExecuteCommandLineCommand(data, commandLineView, commandLinePanel), Keys.ENTER);

		if (settings.centerCommandLine) {
			commandLineView.centerX();
		}

		if (settings.createConsoleView() && settings.repositionConsoleViewWithCommandLine) {
			View<?> consoleView = views.get(consoleViewName);

			consoleView.setX(commandLineView.getRootTable().getX());
			consoleView.setY(commandLineView.getRootTable().getY() + commandLineView.getRootTable().getHeight() + 4);
		}

		addView(commandLineView);

		Shortcut s = keyMap.add(new ToggleViewVisibilityCommand(commandLineView, true), settings.commandLineKeybind);
		commandLinePanel.setViewVisibilityShortcut(s);
	}

	public CommandData getCommandExecutionData () {
		return data;
	}

	public CommandLinePanel getCommandLinePanel () {
		return commandLinePanel;
	}

	public TableView getCommandLineView () {
		return commandLineView;
	}

	public void setCache (CommandCache cache) {
		data.setConsoleCache(cache);
	}

}
