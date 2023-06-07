
package com.vabrant.console.gui;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class ConsoleInputMultiplexer implements InputProcessor {

	private final Array<InputProcessor> processors;
	private final GUIConsole console;
	private final Vector2 touch;

	ConsoleInputMultiplexer (GUIConsole console) {
		this.console = console;
		processors = new Array<>();
		touch = new Vector2();
	}

	public void clear () {
		processors.clear();
	}

	public void add (InputProcessor processor) {
		processors.add(processor);
	}

	@Override
	public boolean keyDown (int keycode) {
		for (InputProcessor p : processors) {
			if (p.keyDown(keycode)) return true;
		}
		return !console.isHidden();
	}

	@Override
	public boolean keyUp (int keycode) {
		for (InputProcessor p : processors) {
			if (p.keyUp(keycode)) return true;
		}
		return !console.isHidden();
	}

	@Override
	public boolean keyTyped (char character) {
		for (InputProcessor p : processors) {
			if (p.keyTyped(character)) return true;
		}
		return false;
	}

	@Override
	public boolean touchDown (int screenX, int screenY, int pointer, int button) {
		console.getStage().screenToStageCoordinates(touch.set(screenX, screenY));
		for (InputProcessor p : processors) {
			if (p.touchDown((int)touch.x, (int)touch.y, pointer, button)) return true;
		}
		return false;
	}

	@Override
	public boolean touchUp (int screenX, int screenY, int pointer, int button) {
		console.getStage().screenToStageCoordinates(touch.set(screenX, screenY));
		for (InputProcessor p : processors) {
			if (p.touchUp((int)touch.x, (int)touch.y, pointer, button)) return true;
		}
		return false;
	}

	@Override
	public boolean touchDragged (int screenX, int screenY, int pointer) {
		return false;
	}

	@Override
	public boolean mouseMoved (int screenX, int screenY) {
		return false;
	}

	@Override
	public boolean scrolled (float amountX, float amountY) {
		return false;
	}
}
