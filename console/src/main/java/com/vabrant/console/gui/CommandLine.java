
package com.vabrant.console.gui;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.utils.StringBuilder;
import com.vabrant.console.CommandExecutionData;
import com.vabrant.console.EventListener;

public class CommandLine extends TextField {

	private boolean skipCharacter;
	private int myCursor;
	private StringBuilder builder;
	private GUIConsole console;
	private CommandLineInput commandLineInput;
	private Color color;
	private CommandExecutionData data;

	private EventListener<ShortcutManager.ExecutedCommandContext> shortcutListener = new EventListener<ShortcutManager.ExecutedCommandContext>() {
		@Override
		public void handleEvent (ShortcutManager.ExecutedCommandContext context) {
			if (!console.getScope().equals(ConsoleScope.COMMAND_LINE)) return;
			skipCharacter = true;
		}
	};

	public CommandLine (CommandExecutionData data, GUIConsole console, Skin skin) {
		super("", skin);
		this.data = data;
		clearListeners();
		setFocusTraversal(false);
		this.console = console;
		builder = new StringBuilder(200);
		commandLineInput = new CommandLineInput();
		color = getStyle().fontColor;

		data.subscribeToEvent(CommandExecutionData.FAIL_EVENT, e -> {
			System.out.println("Event Failed: " + e.getErrorMessage());
			getStyle().fontColor = Color.RED;
		});
		data.subscribeToEvent(CommandExecutionData.SUCCESS_EVENT, e -> {
			clearCommandLine();
		});
	}

	EventListener<ShortcutManager.ExecutedCommandContext> getShortcutEventListener () {
		return shortcutListener;
	}

	public void clearCommandLine () {
		myCursor = 0;
		builder.clear();
		setText("");
		setCursorPosition(0);
	}

	void moveCursor (int amt) {
		myCursor = MathUtils.clamp(myCursor + amt, 0, builder.length());
		setCursorPosition(myCursor);
	}

	public InputAdapter getInput () {
		return commandLineInput;
	}

	private class CommandLineInput extends InputAdapter {

		@Override
		public boolean keyDown (int keycode) {
			if (console.isHidden()) return false;

			getStyle().fontColor = color;

			switch (keycode) {
			case Input.Keys.LEFT:
				moveCursor(-1);
				return true;
			case Input.Keys.RIGHT:
				moveCursor(1);
				return true;
			case Input.Keys.HOME:
				myCursor = 0;
				setCursorPosition(myCursor);
				return true;
			case Input.Keys.END:
				myCursor = builder.length();
				setCursorPosition(myCursor);
				return true;
			default:
				return false;
			}
		}

		@Override
		public boolean keyTyped (char character) {
			if (!console.getScope().equals(ConsoleScope.COMMAND_LINE)) return false;

			if (skipCharacter) {
				skipCharacter = !skipCharacter;
				return false;
			}

			switch (character) {
			// Backspace
			case 8:
				break;
			default:
				if (character < 32) return false;
			}

			switch (character) {
			case 8:
				if (builder.length() == 0) return false;
				builder.deleteCharAt(myCursor - 1);
				setText(builder.toString());
				setCursorPosition(--myCursor);
				return true;
			case '"':
				builder.insert(myCursor, character);
				builder.insert(myCursor, character);
				setText(builder.toString());
				setCursorPosition(++myCursor);
				return true;
			default:
				builder.insert(myCursor, character);
				setText(builder.toString());
				setCursorPosition(++myCursor);
				return true;
			}
		}
	}

}
