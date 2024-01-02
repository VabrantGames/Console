
package com.vabrant.console.commandextension;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.utils.Array;
import com.vabrant.console.*;
import com.vabrant.console.commandextension.gui.CommandLinePanel;
import com.vabrant.console.events.Event;
import com.vabrant.console.events.EventListener;
import com.vabrant.console.gui.views.PanelManagerView;
import com.vabrant.console.gui.views.PanelManagerView.TableSetup;
import com.vabrant.console.gui.GUIConsole;
import com.vabrant.console.log.LogManager;

public class CommandExtension extends ConsoleExtension {

	private String extensionName = "command";
	private boolean centerX = true;
	private int[] commandLineViewVisibilityKeybind = new int[] {Keys.GRAVE};

	private CommandLinePanel commandLinePanel;
	private CommandCache cache;
	private CommandExtensionResultEvent event;
	private LogManager logManager;
	private CommandExtensionCore core;

	public CommandExtension() {
		event = new CommandExtensionResultEvent();
		core = new CommandExtensionCore(this);
	}

//	public void init (Console console) {
//		if (console == null) {
//			throw new ConsoleRuntimeException("GUIConsole is null");
//		}
//
//		if (console instanceof GUIConsole) {
//			GUIConsole guiConsole = (GUIConsole)console;
//			data = new CommandData(guiConsole.getLogManager());
//			CommandExtensionCore strategy = new CommandExtensionCore();
//			strategy.init(data);
//			guiConsole.addStrategy(extensionName, strategy);
//
//			// Core
//
//			KeyMapReference<DefaultKeyMap> cacheKeyMapReference = new KeyMapReference<>();
////			int consoleKeyMapIdx = guiConsole.getKeyMapMultiplexer().indexOf(guiConsole.getKeyMap());
//			int consoleKeyMapIdx = -1;
//
//			if (consoleKeyMapIdx == -1) {
//				guiConsole.getKeyMapMultiplexer().add(cacheKeyMapReference);
//			} else {
//				guiConsole.getKeyMapMultiplexer().insert(consoleKeyMapIdx + 1, cacheKeyMapReference);
//			}
//
//			data.setCacheKeyMapReference(cacheKeyMapReference);
//
//			// GUI
//			commandLinePanel = new CommandLinePanel(data, guiConsole.getSkin());
////			commandLineView = new TableView("CommandLine", new Table(), guiConsole.getSkin(), new RootTableSetup(), 1,
////				commandLinePanel);
////			commandLineView.getRootTable().pack();
////			commandLineView.setWidthPercent(80);
////			commandLineView.setHeightPercent(50);
//
////			if (centerX) {
////				commandLineView.centerX();
////			}
//
////			Shortcut visibilityShortcut = guiConsole.addShortcut(new ToggleViewVisibilityCommand(commandLineView, true),
////				commandLineViewVisibilityKeybind);
////			commandLinePanel.setViewVisibilityShortcut(visibilityShortcut);
////
////			guiConsole.addView(commandLineView);
//		} else {
//			CommandExtensionCore strategy = new CommandExtensionCore();
//			data = new CommandData(null);
//			strategy.init(data);
//			console.addStrategy(extensionName, strategy);
//		}
//	}

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

	public void setConsoleCache (CommandCache cache) {
		this.cache = cache;
	}

	public CommandCache getCache() {
		return cache;
	}

	public CommandExtensionResultEvent getEvent() {
		return event;
	}

	public CommandLinePanel getCommandLinePanel () {
		return commandLinePanel;
	}

	@Override
	public Boolean execute (Object o) throws Exception {
		return core.execute(o);
	}

	@Override
	protected void addedToConsole (Console console) {

		if (console instanceof GUIConsole) {
			//create gui stuff
		}
	}

	private class RootTableSetup implements TableSetup {

		@Override
		public void setup (PanelManagerView view) {
			view.getRootTable().add(view.getContentTable()).grow();
		}
	}

	public static class ExecutionResultEvent implements Event {

		private boolean result;

		protected void setResult (boolean result) {
			this.result = result;
		}

		public boolean getResult() {
			return result;
		}

		@Override
		public <T extends Event> void handle (Array<EventListener<T>> eventListeners) {

		}
	}

}
