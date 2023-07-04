
package com.vabrant.console.test.guitests;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.utils.Logger;
import com.vabrant.console.gui.ConsoleScope;
import com.vabrant.console.gui.GUIConsole;
import com.vabrant.console.annotation.ConsoleMethod;
import com.vabrant.console.annotation.ConsoleObject;
import com.vabrant.console.gui.GUIConsoleCache;
import com.vabrant.console.test.TestMethods;

@ConsoleObject
public class GUIConsoleTest extends ApplicationAdapter {

	public static void main (String[] args) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setWindowedMode(960, 640);
		config.setTitle("GUIConsoleTest");
		new Lwjgl3Application(new GUIConsoleTest(), config);
	}

	private GUIConsole console;
	private GUIConsoleCache cache;

	@Override
	public void create () {
		console = new GUIConsole();
		console.logToSystem(true);
		console.printStackTrackToSystemOnError(true);
		console.getCommandExecutionData().getSettings().setDebugExecutionStrategy(true);
		cache = new GUIConsoleCache();
		cache.setLogLevel(Logger.DEBUG);
		cache.add(new TestMethods(), "methods");
 		cache.add(this, "this");
		console.addShortcut(() -> System.out.println("Hello Console"), Input.Keys.NUM_1);
		cache.addShortcut(() -> System.out.println("Hello World"), Input.Keys.NUM_1);
		console.addShortcut(new int[] {Input.Keys.CONTROL_LEFT, Input.Keys.NUM_2}, () -> System.out.println("Global Shortcut"),
			ConsoleScope.GLOBAL);
		console.setCache(cache);

		Gdx.input.setInputProcessor(console.getInput());
//		Gdx.input.setInputProcessor(console.getStage());
	}

	@Override
	public void resize (int width, int height) {
		console.resize(width, height);
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		console.draw();
	}

	@ConsoleMethod
	public void removeCache () {
		console.setCache(null);
	}

	@ConsoleMethod
	public void setCache () {
		console.setCache(cache);
	}

	@ConsoleMethod
	public void print (String str) {
		System.out.println(str);
	}

	@ConsoleMethod
	public void hello () {
		System.out.println("Hello Console");
	}

	@ConsoleMethod
	public void helloBoolean (boolean b) {
		System.out.println("Hello Boolean(" + b + ")");
	}
}
