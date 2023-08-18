
package com.vabrant.console.test.guitests;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.GL20;
import com.vabrant.console.gui.DefaultGUIConsole;
import com.vabrant.console.gui.GUIConsole;

public class SecondaryInputTest extends ApplicationAdapter {

	private GUIConsole console;

	@Override
	public void create () {
		console = new DefaultGUIConsole();

		InputMultiplexer multi = new InputMultiplexer();
		multi.addProcessor(console.getInput());
		multi.addProcessor(new Controls());
		Gdx.input.setInputProcessor(multi);
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		console.draw();
	}

	private class Controls extends InputAdapter {

		@Override
		public boolean keyTyped (char character) {
			System.out.println(character);
			return true;
		}

		@Override
		public boolean keyDown (int keycode) {
			switch (keycode) {
			case Input.Keys.W:
				System.out.println("Move up");
				break;
			case Input.Keys.S:
				System.out.println("Move down");
				break;
			case Input.Keys.A:
				System.out.println("Move left");
				break;
			case Input.Keys.D:
				System.out.println("Move right");
				break;
			}
			return false;
		}
	}
}
