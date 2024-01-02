
package com.vabrant.console.gui.views;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.FocusListener;
import com.vabrant.console.events.EventListener;
import com.vabrant.console.gui.KeyboardScope;
import com.vabrant.console.gui.shortcuts.DefaultKeyMap;
import com.vabrant.console.gui.shortcuts.GUIConsoleShortcutManager.GUIConsoleExecutedShortcutEvent;
import com.vabrant.console.gui.shortcuts.GUIConsoleShortcutManager.ShortcutManagerFilter;
import com.vabrant.console.gui.shortcuts.Shortcut;
import com.vabrant.console.gui.shortcuts.ShortcutCommand;
import com.vabrant.console.gui.shortcuts.ShortcutManager;
import space.earlygrey.shapedrawer.ShapeDrawer;
import space.earlygrey.shapedrawer.scene2d.ShapeDrawerDrawable;

public class CommandLineView extends DefaultView<Table, DefaultKeyMap> {

	private final String name = "CommandLineView";

	private int[] executeKeybind = new int[] {Keys.ENTER};
	private boolean skipCharacter;
	private boolean clearOnFail;
	private boolean clearOnSuccess = true;
	private TextField textField;
	private Label currentExtensionLabel;
	private ShapeDrawerDrawable labelBackground;
	private Shortcut toggleViewShortcut;
	private Shortcut executeShortcut;
	private Shortcut closeAllViewsShortcut;
	private ToggleKeyListener toggleKeyListener;
	private AlphabeticNumericFilter alphabeticNumericFilter;
	private CommandLineViewShortcutFilter shortcutFilter;

	public CommandLineView (String name, Skin skin, ShapeDrawer shapeDrawer) {
		super(name, new Table(), null, null);

		keyboardScope = new KeyboardScope(name);
		keyMap = new DefaultKeyMap(keyboardScope);
		toggleKeyListener = new ToggleKeyListener();
		alphabeticNumericFilter = new AlphabeticNumericFilter();

		setWidthPercent(80);
		setHeightPercent(30);
		centerX();

//		labelBackground = new ShapeDrawerDrawable(shapeDrawer) {
//			@Override
//			public void drawShapes (ShapeDrawer shapeDrawer, float x, float y, float width, float height) {
//				shapeDrawer.filledRectangle(x, y, width, height, Color.TEAL);
//			}
//		};

		currentExtensionLabel = new Label("", skin);
//		currentExtensionLabel.getStyle().background = labelBackground;

		textField = new TextField("", skin);
		textField.setFocusTraversal(false);
		textField.addCaptureListener(new InputListener() {
			@Override
			public boolean keyTyped (InputEvent event, char character) {
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

		executeShortcut = keyMap.add(new ExecuteCommandLineCommand(this), executeKeybind);
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

	private void buildUI (Skin skin) {
		rootTable.clear();

		String extName = null;

		if (console.getActiveExtension() != null) {
			extName = console.getActiveExtension().getName();
		} else {
			extName = "";
		}

		Table t = new Table();
		t.add(currentExtensionLabel).left().bottom();
		t.row();
		t.add(textField).width(rootTable.getWidth() * 0.80f).left().bottom();
		rootTable.add(t).expand().bottom().padBottom(10);
	}

	private void focusTextField () {
		console.getStage().setKeyboardFocus(textField);
		console.subscribeToEvent(GUIConsoleExecutedShortcutEvent.class, toggleKeyListener);
	}

	@Override
	public boolean show (boolean focus) {
		if (super.show(focus)) {
			buildUI(getGUIConsole().getSkin());
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
			if (toggleViewShortcut != null && context.getKeybindPacked() == toggleViewShortcut.getKeybindPacked() ||
			context.getKeybindPacked() == executeShortcut.getKeybindPacked() ||
			closeAllViewsShortcut != null && context.getKeybindPacked() == closeAllViewsShortcut.getKeybindPacked() ||
			shortcutFilter != null && shortcutFilter.accept(context.getKeybindPacked())) return true;

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

	public interface CommandLineViewShortcutFilter {
		boolean accept(int currentlyPressedKeysPacked);
	}

	public static class ExecuteCommandLineCommand implements ShortcutCommand {

		private CommandLineView view;

		public ExecuteCommandLineCommand (CommandLineView view) {
			if (view == null) throw new IllegalArgumentException("View can't be null");
			this.view = view;
		}

		@Override
		public void execute () {
			if (view.isHidden()) return;
			boolean success = view.getGUIConsole().execute(view.getTextField().getText().trim());

			if (view.clearOnSuccess && success) {
				view.clearCommandLine();
			}
		}
	}

}