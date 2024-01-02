
package com.vabrant.console.test.guitests;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.vabrant.console.DebugLogger;
import com.vabrant.console.gui.*;
import com.vabrant.console.commandextension.annotation.ConsoleReference;
import com.vabrant.console.gui.shortcuts.DefaultKeyMap;
import com.vabrant.console.test.GUITestLauncher.WindowSize;

@ConsoleReference
@WindowSize(width = 1080, height = 720)
public class GUIConsoleTest extends ApplicationAdapter {

	private GUIConsole console;

	@Override
	public void create () {
		console = new DefaultGUIConsole();
		console.getLogger().setLevel(DebugLogger.DEBUG);
		console.addGlobalShortcut( () -> System.out.println("Hello Console"), new int[] {Input.Keys.NUM_1});

		Gdx.input.setInputProcessor(console.getInput());
	}

	private static class TestFocusObject implements FocusObject {

		@Override
		public void focus () {
			System.out.println("Hello TestFocusObject");
		}

		@Override
		public void unfocus () {
			System.out.println("Goodbye TestFocusObject");
		}

		@Override
		public KeyboardScope getKeyboardScope () {
			return null;
		}

		@Override
		public DefaultKeyMap getKeyMap () {
			return null;
		}

		@Override
		public boolean lockFocus () {
			return true;
		}

		@Override
		public String getName () {
			return "TestFocusObject";
		}
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
