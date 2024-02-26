
package com.vabrant.console.gui.views;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle;
import com.badlogic.gdx.scenes.scene2d.utils.FocusListener;
import com.badlogic.gdx.utils.Array;
import com.vabrant.console.ConsoleRuntimeException;
import com.vabrant.console.Utils;
import com.vabrant.console.events.ConsoleExtensionChangeEvent;
import com.vabrant.console.events.EventListener;
import com.vabrant.console.gui.DefaultKeyboardScope;
import com.vabrant.console.gui.GUIConsole;
import com.vabrant.console.gui.shortcuts.DefaultKeyMap;
import com.vabrant.console.gui.shortcuts.GUIConsoleShortcutManager.GUIConsoleExecutedShortcutEvent;
import com.vabrant.console.gui.shortcuts.GUIConsoleShortcutManager.ShortcutManagerFilter;
import com.vabrant.console.gui.shortcuts.Shortcut;
import com.vabrant.console.gui.shortcuts.ShortcutManager;
import com.vabrant.console.log.LogLevel;
import space.earlygrey.shapedrawer.ShapeDrawer;
import space.earlygrey.shapedrawer.scene2d.ShapeDrawerDrawable;

public class CommandLineView extends DefaultView {

	private int resetColorFlag;
	public static final String EXECUTE_SHORTCUT_ID = "Execute";

	private final String name = "CommandLineView";

	private int[] executeKeybind = new int[] {Keys.ENTER};
	private boolean skipCharacter;
	private boolean clearOnFail;
	private boolean clearOnSuccess = true;
	private TextField textField;
	private Label currentExtensionLabel;
	private Table extensionTable;
	private Shortcut toggleViewShortcut;
	private Shortcut executeShortcut;
	private Shortcut closeAllViewsShortcut;
	private ToggleKeyListener toggleKeyListener;
	private AlphabeticNumericFilter alphabeticNumericFilter;
	private CommandLineViewShortcutFilter shortcutFilter;
	private Color errorColor;
	private Color textFieldFontColor;

	public CommandLineView (String name, Skin skin, ShapeDrawer shapeDrawer) {
		super(name, new Table(), new Table());

		keyboardScope = new DefaultKeyboardScope(name);
		keyMap = new DefaultKeyMap(keyboardScope);
		toggleKeyListener = new ToggleKeyListener();
		alphabeticNumericFilter = new AlphabeticNumericFilter();
		errorColor = new Color(0xFF7842FF);

		executeShortcut = keyMap.register(EXECUTE_SHORTCUT_ID, new ExecuteCommandLineCommand(this), executeKeybind);

		setWidthPercent(80);
		setHeightPercent(30);
		centerX();

		extensionTable = new Table() {
			@Override
			public void draw (Batch batch, float parentAlpha) {
				if (!currentExtensionLabel.getText().isEmpty()) {
					super.draw(batch, parentAlpha);
				}
			}
		};
		extensionTable.setBackground(new ShapeDrawerDrawable(shapeDrawer) {
			@Override
			public void drawShapes (ShapeDrawer shapeDrawer, float x, float y, float width, float height) {
				shapeDrawer.filledRectangle(x, y, width, height, Color.TEAL);
			}
		});

		currentExtensionLabel = new Label("", new LabelStyle(skin.get(LabelStyle.class)));
		currentExtensionLabel.setEllipsis(true);
		extensionTable.add(currentExtensionLabel).growX().padLeft(5).padRight(5);

		textField = new TextField("", new TextFieldStyle(skin.get(TextFieldStyle.class)));
		textField.setFocusTraversal(false);
		textFieldFontColor = textField.getStyle().fontColor;

		textField.addCaptureListener(new InputListener() {
			@Override
			public boolean keyTyped (InputEvent event, char character) {
				if (Utils.isBitOn(resetColorFlag, 1)) {
					if (Utils.isBitOn(resetColorFlag, 2)) {
						resetColorFlag = Utils.setBit(resetColorFlag, 2, 0);
					} else {
						resetColorFlag = 0;
						textField.getStyle().fontColor = Color.WHITE;
					}
				}

				if (skipCharacter) {
					skipCharacter = false;
					event.stop();
					return true;
				}
				return false;
			}
		});

		textField.addListener(new FocusListener() {
			@Override
			public void keyboardFocusChanged (FocusEvent event, Actor actor, boolean focused) {
				if (focused) {
					console.getShortcutManager().setKeycodeFilter(alphabeticNumericFilter);
				} else {
					console.getShortcutManager().setKeycodeFilter(null);
				}
			}
		});

		buildUI(skin);
	}

	@Override
	public void resize (float oldWidth, float oldHeight, float width, float height) {
		super.resize(oldWidth, oldHeight, width, height);

		buildUI(console.getSkin());
	}

	@Override
	public void focus () {
		super.focus();
		focusTextField();
	}

	public void setToggleViewShortcut (Shortcut shortcut) {
		toggleViewShortcut = shortcut;
	}

	public void setCloseAllViewsShortcut (Shortcut shortcut) {
		closeAllViewsShortcut = shortcut;
	}

	@Override
	public void setTitleBar (String title, TitleBar titleBar) {
		throw new ConsoleRuntimeException("Operation not supported");
	}

	private void buildUI (Skin skin) {
		contentTable.clearChildren();

		Table t = new Table();
		t.add(extensionTable).left().bottom();
		t.row();
		t.add(textField).width(rootTable.getWidth() * 0.80f).left().bottom();
		contentTable.add(t).expand().bottom().padBottom(10);
	}

	private void focusTextField () {
		console.getStage().setKeyboardFocus(textField);
		console.subscribeToEvent(GUIConsoleExecutedShortcutEvent.class, toggleKeyListener);
	}

	@Override
	public void setGUIConsole (GUIConsole console) {
		super.setGUIConsole(console);

		console.subscribeToEvent(ConsoleExtensionChangeEvent.class, new UpdateExtensionNameListener());
	}

	@Override
	public boolean show (boolean focus) {
		if (super.show(focus)) {
			focusTextField();
			return true;
		}
		return false;
	}

	@Override
	public void hide () {
		super.hide();
		console.unsubscribeFromEvent(GUIConsoleExecutedShortcutEvent.class, toggleKeyListener);
	}

	public TextField getTextField () {
		return textField;
	}

	public void clearCommandLine () {
		textField.setText("");
	}

	private class ToggleKeyListener implements EventListener<GUIConsoleExecutedShortcutEvent> {

		@Override
		public void handleEvent (GUIConsoleExecutedShortcutEvent GUIConsoleExecutedShortcutEvent) {
			if (!textField.hasKeyboardFocus() || toggleViewShortcut == null) return;
			if (toggleViewShortcut.getKeybindPacked() == GUIConsoleExecutedShortcutEvent.getKeybindPacked()) {
				skipCharacter = true;
			}
		}
	}

	public class AlphabeticNumericFilter implements ShortcutManagerFilter {
		@Override
		public boolean acceptKeycodeTyped (GUIConsoleExecutedShortcutEvent context, int keycode) {
			if (toggleViewShortcut != null && context.getKeybindPacked() == toggleViewShortcut.getKeybindPacked()
				|| context.getKeybindPacked() == executeShortcut.getKeybindPacked()
				|| closeAllViewsShortcut != null && context.getKeybindPacked() == closeAllViewsShortcut.getKeybindPacked()
				|| shortcutFilter != null && shortcutFilter.accept(context.getKeybindPacked())) return true;

			boolean isShiftPressed = ShortcutManager.isShiftPressed(context.getKeybind());
			boolean isControlPressed = ShortcutManager.isControlPressed(context.getKeybind());
			boolean isAltSymPressed = ShortcutManager.isAltSymPressed(context.getKeybind());

			if (!isShiftPressed && !isControlPressed && !isAltSymPressed) {
				return false;
			} else if (isShiftPressed && !isControlPressed && !isAltSymPressed) {
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
				case Keys.NUMPAD_0:
				case Keys.NUMPAD_1:
				case Keys.NUMPAD_2:
				case Keys.NUMPAD_3:
				case Keys.NUMPAD_4:
				case Keys.NUMPAD_5:
				case Keys.NUMPAD_6:
				case Keys.NUMPAD_7:
				case Keys.NUMPAD_8:
				case Keys.NUMPAD_9:
				case Keys.MINUS:
				case Keys.EQUALS:
				case Keys.LEFT_BRACKET:
				case Keys.RIGHT_BRACKET:
				case Keys.COLON:
				case Keys.APOSTROPHE:
				case Keys.COMMA:
				case Keys.PERIOD:
				case Keys.SLASH:
				case Keys.BACKSLASH:
				case Keys.GRAVE:
				case Keys.HOME:
				case Keys.END:
				case Keys.INSERT:
				case Keys.FORWARD_DEL:
				case Keys.LEFT:
				case Keys.RIGHT:
					return false;
				}
			} else if (isControlPressed && !isShiftPressed && !isAltSymPressed) {
				switch (keycode) {
				case Keys.V:
				case Keys.C:
				case Keys.INSERT:
				case Keys.X:
				case Keys.A:
				case Keys.Z:
					break;
				}
			}
			return true;
		}
	}

	private class UpdateExtensionNameListener implements EventListener<ConsoleExtensionChangeEvent> {

		@Override
		public void handleEvent (ConsoleExtensionChangeEvent o) {
			if (o != null) {
				currentExtensionLabel.setText(o.getExtension().getName());
				contentTable.invalidateHierarchy();
			}
		}
	}

	public interface CommandLineViewShortcutFilter {
		boolean accept (int currentlyPressedKeysPacked);
	}

	public class ExecuteCommandLineCommand implements Runnable {

		private CommandLineView view;

		public ExecuteCommandLineCommand (CommandLineView view) {
			if (view == null) throw new IllegalArgumentException("View can't be null");
			this.view = view;
		}

		@Override
		public void run () {
			if (view.isHidden()) return;
			String text = getTextField().getText().trim();
			boolean success = view.getGUIConsole().execute(text);

			if (view.clearOnSuccess && success) {
				view.clearCommandLine();
			} else if (!success) {
				view.textField.getStyle().fontColor = errorColor;

				// Set 1st and 2nd bit
				resetColorFlag = 6;

				view.getGUIConsole().getLogManager().add(null, "No command found: " + text, LogLevel.ERROR);
			}
		}
	}

	public static class CommandLineSettings {

		private Array<com.badlogic.gdx.scenes.scene2d.EventListener> textFieldListeners;

		public void addTextFieldListener (com.badlogic.gdx.scenes.scene2d.EventListener listener) {
			textFieldListeners.add(listener);
		}
	}

}
