
package com.vabrant.console.commandextension;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.vabrant.console.Console;
import com.vabrant.console.ConsoleRuntimeException;
import com.vabrant.console.commandextension.gui.CommandLinePanel;
import com.vabrant.console.gui.DefaultView;
import com.vabrant.console.gui.DefaultView.TableSetup;
import com.vabrant.console.gui.GUIConsole;
import com.vabrant.console.gui.TableView;
import com.vabrant.console.gui.commands.ToggleViewVisibilityCommand;
import com.vabrant.console.gui.shortcuts.DefaultKeyMap;
import com.vabrant.console.gui.shortcuts.KeyMapReference;
import com.vabrant.console.gui.shortcuts.Shortcut;

public class CommandExtension {

	private String extensionName = "command";
	private boolean centerX = true;
	private int[] commandLineViewVisibilityKeybind = new int[] {Keys.GRAVE};

	private TableView commandLineView;
	private CommandLinePanel commandLinePanel;
	private CommandData data;

	public void init (Console console) {
		if (console == null) {
			throw new ConsoleRuntimeException("GUIConsole is null");
		}

		if (console instanceof GUIConsole) {
			GUIConsole guiConsole = (GUIConsole)console;
			data = new CommandData(guiConsole.getLogManager());
			CommandStrategy strategy = new CommandStrategy();
			strategy.init(data);
			guiConsole.addStrategy(extensionName, strategy);

			// Core

			KeyMapReference<DefaultKeyMap> cacheKeyMapReference = new KeyMapReference<>();
			int consoleKeyMapIdx = guiConsole.getKeyMapMultiplexer().indexOf(guiConsole.getKeyMap());

			if (consoleKeyMapIdx == -1) {
				guiConsole.getKeyMapMultiplexer().add(cacheKeyMapReference);
			} else {
				guiConsole.getKeyMapMultiplexer().insert(consoleKeyMapIdx + 1, cacheKeyMapReference);
			}

			data.setCacheKeyMapReference(cacheKeyMapReference);

			// GUI
			commandLinePanel = new CommandLinePanel(data, guiConsole.getSkin());
			commandLineView = new TableView("CommandLine", new Table(), guiConsole.getSkin(), new RootTableSetup(), 1,
				commandLinePanel);
			commandLineView.getRootTable().pack();
			commandLineView.setWidthPercent(80);
			commandLineView.setHeightPercent(50);

			if (centerX) {
				commandLineView.centerX();
			}

			Shortcut visibilityShortcut = guiConsole.addShortcut(new ToggleViewVisibilityCommand(commandLineView, true),
				commandLineViewVisibilityKeybind);
			commandLinePanel.setViewVisibilityShortcut(visibilityShortcut);

			guiConsole.addView(commandLineView);
		} else {
			CommandStrategy strategy = new CommandStrategy();
			data = new CommandData(null);
			strategy.init(data);
			console.addStrategy(extensionName, strategy);
		}
	}

	public void setExtensionName (String name) {
		extensionName = name;
	}

	public void centerCommandLineView (boolean center) {
		centerX = center;
	}

	public void setExecuteCommandKeybind (int[] keybind) {
		commandLinePanel.setExecuteKeybind(keybind);
	}

	public void setCommandLineViewVisibilityKeybind (int[] keybind) {
		commandLineViewVisibilityKeybind = keybind;
	}

	public CommandData getData () {
		return data;
	}

	public CommandLinePanel getCommandLinePanel () {
		return commandLinePanel;
	}

	public TableView getCommandLineView () {
		return commandLineView;
	}

	private class RootTableSetup implements TableSetup {

		@Override
		public void setup (DefaultView<?> view) {
			view.getRootTable().add(view.getContentTable()).grow();
		}
	}

}
