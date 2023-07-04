
package com.vabrant.console.commandstrategy.gui;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.FocusListener;
import com.kotcrab.vis.ui.VisUI;
import com.vabrant.console.CommandExecutionData;
import com.vabrant.console.CommandExecutionSettings;
import com.vabrant.console.EventListener;
import com.vabrant.console.gui.*;
import com.vabrant.console.gui.ShortcutManager.ShortcutManagerContext;
import com.vabrant.console.gui.ShortcutManager.ShortcutManagerFilter;

public class CommandLinePanel extends Panel {

	private boolean skipCharacter;
	private int myCursor;
	private TextField textField;
	private StringBuilder builder;
	private Color defaultTextColor;
	private CommandExecutionData data;

	public CommandLinePanel (ShortcutManager shortcutManager, CommandExecutionSettings settings) {
		this(shortcutManager, new TextField("", VisUI.getSkin()), settings);
	}

	public CommandLinePanel (ShortcutManager shortcutManager, TextField textField, CommandExecutionSettings settings) {
		super("CommandLine");
		this.textField = textField;

		builder = new StringBuilder(200);

		textField.setFocusTraversal(false);

		if (settings.useCustomTextFieldInput()) {
			textField.clearListeners();
			textField.addListener(new CommandLineInput());
		}

		textField.addCaptureListener(new Focus());

		textField.addCaptureListener(new InputListener() {
			@Override
			public boolean keyTyped (InputEvent event, char character) {
				if (!getView().getConsole().getScope().equals(getName())) return false;
				if (skipCharacter) {
					skipCharacter = !skipCharacter;
					event.stop();
					return true;
				}
				return false;
			}
		});

		defaultTextColor = textField.getStyle().fontColor;
		contentTable.add(textField).expand().top().fillX();

// shortcutManager.subscribeToExecutedEvent(new EventListener<ShortcutManagerContext>() {
// @Override
// public void handleEvent (ShortcutManagerContext shortcutManagerContext) {
// GUIConsole console = getView().getConsole();
// if (!console.getScope().equals(getName())) return;
// skipCharacter = true;
// }
// });
	}

	public void clearCommandLine () {
		myCursor = 0;
		textField.setText("");
		textField.setCursorPosition(0);
	}

	void moveCursor (int amt) {
		myCursor = MathUtils.clamp(myCursor + amt, 0, builder.length());
		textField.setCursorPosition(myCursor);
	}

	public TextField getTextField () {
		return textField;
	}

	@Override
	public void focus () {
		super.focus();
		getView().getConsole().getStage().setKeyboardFocus(textField);
	}

	private class Focus extends FocusListener {

		private ShortcutManagerAlphabeticNumericFilter filter = new ShortcutManagerAlphabeticNumericFilter();
		private EventListener<ShortcutManagerContext> executedCommandListener = new EventListener<ShortcutManagerContext>() {
			@Override
			public void handleEvent (ShortcutManagerContext shortcutManagerContext) {
				GUIConsole console = getView().getConsole();
				if (!console.getScope().equals(getName())
					|| shortcutManagerContext.getKeybindPacked() == getView().getVisibilityKeybindPacked()) return;
				skipCharacter = true;
			}
		};

		@Override
		public void keyboardFocusChanged (FocusEvent event, Actor actor, boolean focused) {
			ShortcutManager manager = getView().getConsole().getShortcutManager();

			if (focused) {
				manager.setKeycodeFilter(filter);
				manager.setExecutedCommandListener(executedCommandListener);
			} else {
				manager.setKeycodeFilter(null);
				manager.setExecutedCommandListener(null);
			}
		}
	}

	public class ShortcutManagerAlphabeticNumericFilter implements ShortcutManagerFilter {
		@Override
		public boolean acceptKeycodeTyped (ShortcutManagerContext context, int keycode) {

			if (context.getKeybindPacked() == getView().getVisibilityKeybindPacked()) return true;

			switch (keycode) {
			case Keys.A:
			case Keys.B:
			case Keys.C:
			case Keys.D:
			case Keys.E:
			case Keys.F:
			case Keys.G:
			case Keys.H:
			case Keys.I:
			case Keys.J:
			case Keys.K:
			case Keys.L:
			case Keys.M:
			case Keys.N:
			case Keys.O:
			case Keys.P:
			case Keys.Q:
			case Keys.R:
			case Keys.S:
			case Keys.T:
			case Keys.U:
			case Keys.V:
			case Keys.W:
			case Keys.X:
			case Keys.Y:
			case Keys.Z:
			case Keys.NUM_0:
			case Keys.NUM_1:
			case Keys.NUM_2:
			case Keys.NUM_3:
			case Keys.NUM_4:
			case Keys.NUM_5:
			case Keys.NUM_6:
			case Keys.NUM_7:
			case Keys.NUM_8:
			case Keys.NUM_9:
				return false;
			default:
				return true;
			}
		}
	}

	private class CommandLineInput extends InputListener {

		@Override
		public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
			getView().getConsole().getStage().setKeyboardFocus(textField);
			return false;
		}

		@Override
		public boolean keyDown (InputEvent event, int keycode) {
// if (getView().isHidden()) return false;

			textField.getStyle().fontColor = defaultTextColor;

			switch (keycode) {
			case Keys.LEFT:
				moveCursor(-1);
				return true;
			case Keys.RIGHT:
				moveCursor(1);
				return true;
			case Keys.HOME:
				myCursor = 0;
				textField.setCursorPosition(myCursor);
				return true;
			case Keys.END:
				myCursor = builder.length();
				textField.setCursorPosition(myCursor);
				return true;
			default:
				return false;
			}
		}

		@Override
		public boolean keyTyped (InputEvent event, char character) {
			System.out.println("typed");
// if (skipCharacter) {
// skipCharacter = !skipCharacter;
// return false;
// }

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
				textField.setText(builder.toString());
				textField.setCursorPosition(--myCursor);
				return true;
			case '"':
				builder.insert(myCursor, character);
				builder.insert(myCursor, character);
				textField.setText(builder.toString());
				textField.setCursorPosition(++myCursor);
				return true;
			default:
				builder.insert(myCursor, character);
				textField.setText(builder.toString());
				textField.setCursorPosition(++myCursor);
				return true;
			}
		}
	}

}
