
package com.vabrant.console.test.guitests;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.ScreenUtils;
import com.vabrant.console.ConsoleCache;
import com.vabrant.console.commandextension.GUICommandConsoleConfiguration;
import com.vabrant.console.gui.shortcuts.ConsoleCommand;
import com.vabrant.console.DebugLogger;
import com.vabrant.console.commandextension.GUICommandConsole;
import com.vabrant.console.test.TestMethods;

//@Deprecated
public class ShortcutManagerKeyTest extends ApplicationAdapter {

	public static void main (String[] args) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setWindowedMode(1080, 720);
		config.setTitle("ShortcutManagerTest");
		new Lwjgl3Application(new ShortcutManagerKeyTest(), config);
	}

	private GUICommandConsole console;

	@Override
	public void create () {
		Gdx.app.setLogLevel(Application.LOG_DEBUG);

		GUICommandConsoleConfiguration settings = new GUICommandConsoleConfiguration();
		settings.logToSystem(true);
// settings.toggleConsoleViewKeybind(Keys.GRAVE);
		settings.consoleViewHeightPercent(60);
// settings.createConsoleView(false);

		console = new GUICommandConsole(null, null, settings);
		console.getLogger().setLevel(DebugLogger.DEBUG);
		console.addShortcut( () -> System.out.println("Hello"), Keys.A);

		ConsoleCache cache = new ConsoleCache();
		cache.add(new TestMethods(), "test");
		console.setCache(cache);

// DefaultKeyMap keyMap = console.getConsoleView().getPanel("CommandLine").getKeyMap();

// KeyMap keymap = new KeyMap("bob");
// keyMap.add(() -> System.out.println("Bob"), Keys.NUM_2);

// ConsoleTestsUtils.executePrivateMethod(console.shortcutManager, "setPanelKeyMap", new Class[] {KeyMap.class}, keymap);
// ConsoleTestsUtils.executePrivateMethod(console, "setScope", new Class[] {String.class}, "bob");

// manager.add(new int[] {Keys.CONTROL_LEFT, Keys.SHIFT_LEFT, Keys.O}, new PrintCommand("Hello Space"));
		Gdx.input.setInputProcessor(console.getInput());
	}

	@Override
	public void render () {
		ScreenUtils.clear(Color.WHITE);
		console.draw();
	}

	private static class PrintCommand implements ConsoleCommand {

		final String str;

		PrintCommand (String str) {
			this.str = str;
		}

		@Override
		public void execute () {
			System.out.println(str);
		}
	}
}
