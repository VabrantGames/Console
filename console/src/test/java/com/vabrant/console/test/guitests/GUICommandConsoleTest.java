
package com.vabrant.console.test.guitests;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.ScreenUtils;
import com.vabrant.console.commandextension.CommandCache;
import com.vabrant.console.commandextension.CommandConsoleConfiguration;
import com.vabrant.console.DebugLogger;
import com.vabrant.console.commandextension.CommandConsole;
import com.vabrant.console.test.TestMethods;

public class GUICommandConsoleTest extends ApplicationAdapter {

	public static void main (String[] args) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setWindowedMode(1080, 720);
		config.setTitle("ShortcutManagerTest");
		new Lwjgl3Application(new GUICommandConsoleTest(), config);
	}

	private CommandConsole console;

	@Override
	public void create () {
		Gdx.app.setLogLevel(Application.LOG_DEBUG);

		CommandConsoleConfiguration config = new CommandConsoleConfiguration();
		config.logToSystem(true);

		console = new CommandConsole(null, null, config);
		console.getLogger().setLevel(DebugLogger.DEBUG);
		console.getView(config.getConsoleViewName()).getLogger().setLevel(DebugLogger.DEBUG);
		console.getCommandLineView().getLogger().setLevel(DebugLogger.DEBUG);
		console.addShortcut( () -> System.out.println("Hello"), new int[] {Keys.A});
		console.getCommandExecutionData().getConsoleStrategy().getLogger().setLevel(DebugLogger.DEBUG);

		CommandCache cache = new CommandCache();
		cache.getLogger().setLevel(DebugLogger.DEBUG);
		cache.addAll(new TestMethods(), "test");
		console.setCache(cache);

		Gdx.input.setInputProcessor(console.getInput());
	}

	@Override
	public void render () {
		ScreenUtils.clear(Color.WHITE);
		console.draw();
	}

}
