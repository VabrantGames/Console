
package com.vabrant.console.test.guitests;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.graphics.GL20;
import com.vabrant.console.gui.GUIConsole;
import com.vabrant.console.annotation.ConsoleMethod;
import com.vabrant.console.annotation.ConsoleObject;

@ConsoleObject
public class GUIConsoleTest extends ApplicationAdapter {

	public static void main (String[] args) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setWindowedMode(960, 640);
		config.setTitle("GUIConsoleTest");
		new Lwjgl3Application(new GUIConsoleTest(), config);
	}

	private GUIConsole console;

	@Override
	public void create () {
		console = new GUIConsole();
		console.logToSystem(true);
		console.printStackTrackToSystemOnError(true);
		console.addShortcut( () -> System.out.println("Hello Console"), Input.Keys.NUM_1);

		Gdx.input.setInputProcessor(console.getInput());
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

}
