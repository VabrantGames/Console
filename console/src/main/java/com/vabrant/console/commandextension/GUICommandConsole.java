
package com.vabrant.console.commandextension;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.vabrant.console.*;
import com.vabrant.console.commandextension.gui.CommandLinePanel;
import com.vabrant.console.gui.*;
import com.vabrant.console.gui.commands.ToggleViewVisibilityCommand;
import com.vabrant.console.gui.shortcuts.Shortcut;

public class GUICommandConsole extends GUIConsole {

	private TableView commandLineView;
	private CommandLinePanel commandLinePanel;
	private CommandExecutionData data;

	public GUICommandConsole () {
		this(null);
	}

	public GUICommandConsole (Batch batch) {
		this(null, null, null);
	}

	public GUICommandConsole (Batch batch, Skin skin, GUICommandConsoleConfiguration settings) {
		super(batch, skin, null);

		if (settings == null) {
			settings = new GUICommandConsoleConfiguration();
		}

		boolean hasConsoleView = settings.createConsoleView();

		CommandExtensionSettings commandExtensionSettings = settings.getCommandExtensionSettings();
		if (commandExtensionSettings == null) {
			commandExtensionSettings = new CommandExtensionSettings();
		}

		data = new CommandExecutionData(logManager, commandExtensionSettings);
		CommandExecutionStrategy commandExecutionStrategy = new CommandExecutionStrategy();
		commandExecutionStrategy.init(data);

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

	public CommandExecutionData getCommandExecutionData () {
		return data;
	}

	public CommandLinePanel getCommandLinePanel () {
		return commandLinePanel;
	}

	public TableView getCommandLineView () {
		return commandLineView;
	}

	public void setCache (ConsoleCache cache) {
		data.setConsoleCache(cache);
	}

}
