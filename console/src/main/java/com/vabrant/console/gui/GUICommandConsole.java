
package com.vabrant.console.gui;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.vabrant.console.CommandExecutionSettings;
import com.vabrant.console.commandstrategy.gui.CommandLinePanel;
import com.vabrant.console.gui.commands.CloseAllViewsCommand;
import com.vabrant.console.gui.commands.ToggleVisibilityViewCommand;

public class GUICommandConsole extends GUIConsole {

	public static final int[] COMMANND_LINE_KEYBIND = {Keys.GRAVE};
	public static final int[] CONSOLE_VIEW_KEYBIND = {Keys.CONTROL_LEFT, Keys.GRAVE};

	private TableView commandLineView;
	private MultiPanelWindowView consoleView;
	private CommandExecutionSettings settings;

	public GUICommandConsole () {
		this(null);
	}

	public GUICommandConsole (Batch batch) {
		this(null, null, null);
	}

	public GUICommandConsole (Batch batch, Skin skin, CommandExecutionSettings settings) {
		super(batch, skin);

		if (settings == null) {
			 settings = new CommandExecutionSettings();
		}

		commandLineView = new TableView("CommandLine", new CommandLinePanel(getShortcutManager(), settings));
		commandLineView.getRootTable().pack();
		commandLineView.setWidthPercent(80);
		commandLineView.centerX();
		addView(commandLineView);

		consoleView = new MultiPanelWindowView("ConsoleView");
		consoleView.setY(commandLineView.getRootTable().getY() + commandLineView.getRootTable().getHeight());
		consoleView.addPanel(new CommandLinePanel(getShortcutManager(), settings));
		consoleView.addPanel(new LogPanel("Log", null));
		addView(consoleView);

		int commandLineViewKeybindPacked = keyMap.add(new ToggleVisibilityViewCommand(commandLineView, true), COMMANND_LINE_KEYBIND);
		int consoleViewKeybindPacked = keyMap.add(new ToggleVisibilityViewCommand(consoleView, true), CONSOLE_VIEW_KEYBIND);
		consoleView.setVisibilityKeybindPacked(consoleViewKeybindPacked);
		commandLineView.setVisibilityKeybindPacked(commandLineViewKeybindPacked);
		keyMap.add(new CloseAllViewsCommand(this), Keys.ESCAPE);
	}

	public TableView getCommandLineView () {
		return commandLineView;
	}

	public MultiPanelWindowView getConsoleView () {
		return consoleView;
	}

}
