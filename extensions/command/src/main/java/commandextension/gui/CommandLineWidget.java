
package commandextension.gui;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import commandextension.CommandData;

@Deprecated
public class CommandLineWidget {

	private boolean skipCharacter = true;
	private int myCursor;
	private TextField textField;
	private StringBuilder builder;
	private CommandData data;
	private CommandLineWidgetSettings settings;

	public CommandLineWidget (CommandData data, Skin skin, boolean useCustomInput) {
		this.data = data;
		textField = new TextField("", skin);
		textField.setFocusTraversal(false);
		builder = new StringBuilder();

		settings = new CommandLineWidgetSettings();
		textField.setTextFieldListener(new KeyListener());
		textField.setMessageText("Helllooooo");
	}

	public void clearCommandLine () {
		myCursor = 0;
		builder.setLength(0);
		textField.setText("");
		textField.setCursorPosition(0);
	}

	void moveCursor (int amt) {
		myCursor = MathUtils.clamp(myCursor + amt, 0, builder.length());
		textField.setCursorPosition(myCursor);
	}

	void setCursor (int pos) {
		myCursor = MathUtils.clamp(pos, 0, builder.length());
		textField.setCursorPosition(myCursor);
	}

	public String getText () {
		return textField.getText();
	}

	public TextField getTextField () {
		return textField;
	}

	public void skipCharacter () {
		skipCharacter = true;
	}

	public void resetSkipCharacter () {
		skipCharacter = false;
	}

	public boolean shouldSkipCharacter () {
		return skipCharacter;
	}

	public static class CommandLineWidgetSettings {

		public boolean addClosingParenthesis;
		public boolean addClosingQuote;
	}

	private class KeyListener implements TextFieldListener {

		@Override
		public void keyTyped (TextField textField, char c) {
			if (settings == null) return;

			switch (c) {
			case '"':
				if (settings.addClosingQuote) {
					int pos = textField.getCursorPosition();
					textField.appendText("\"");
					textField.setCursorPosition(pos);
				}
				break;
			case '(':
				if (settings.addClosingParenthesis) {
					int pos = textField.getCursorPosition();
					textField.appendText(")");
					textField.setCursorPosition(pos);
				}
			}
		}
	}

	private class CommandLineInput extends ClickListener {

		@Override
		public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
			if (textField.getStage() != null) {
				textField.getStage().setKeyboardFocus(textField);
			}
			return false;
		}

		@Override
		public boolean keyDown (InputEvent event, int keycode) {
			return true;
		}

		@Override
		public boolean keyUp (InputEvent event, int keycode) {
			return true;
		}

		@Override
		public boolean keyTyped (InputEvent event, char character) {
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
			case '(':
				builder.insert(myCursor, ')');
				builder.insert(myCursor, character);
				textField.setText(builder.toString());
				textField.setCursorPosition(++myCursor);
				return true;
			default:
				if (character < 32) return false;
				builder.insert(myCursor, character);
				textField.setText(builder.toString());
				textField.setCursorPosition(++myCursor);
				return false;
			}
		}
	}

}
