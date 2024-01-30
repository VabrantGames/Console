
package com.vabrant.console.commandextension.gui;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.FocusListener;
import com.vabrant.console.commandextension.CommandData;
import com.vabrant.console.events.EventListener;
import com.vabrant.console.ConsoleExtension;
import com.vabrant.console.gui.shortcuts.*;
import com.vabrant.console.gui.shortcuts.GUIConsoleShortcutManager.GUIConsoleExecutedShortcutEvent;
import com.vabrant.console.gui.shortcuts.GUIConsoleShortcutManager.ShortcutManagerFilter;

@Deprecated
public class CommandLinePanel {

	private int[] executeKeybind = new int[] {Keys.ENTER};
	private boolean clearOnFail;
	private boolean clearOnSuccess = true;
	private CommandData data;
	private CommandLineWidget widget;
	private Shortcut viewVisibilityShortcut;

	public CommandLinePanel (CommandData data, Skin skin) {
		this(skin, data, true);
	}

	public CommandLinePanel (Skin skin, CommandData data, boolean customInput) {
// super("CommandLine", Table.class, KeyMap.class);

		widget = new CommandLineWidget(data, skin, customInput);

		widget.getTextField().addCaptureListener(new Focus());

		widget.getTextField().addCaptureListener(new InputListener() {
			@Override
			public boolean keyTyped (InputEvent event, char character) {
// if (!getView().getConsole().getKeyboardScope().equals(scope)) return false;
				if (widget.shouldSkipCharacter()) {
					widget.resetSkipCharacter();
					event.stop();
					return true;
				}
				return false;
			}
		});

		if (customInput) {
			widget.getTextField().addListener(new ClickListener() {
				@Override
				public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
// getView().getConsole().getStage().setKeyboardFocus(widget.getTextField());
					return false;
				}
			});
		}

// contentTable.add(widget.getTextField()).expand().fillX().bottom().padBottom(10).minHeight(40);

		ConsoleExtension strat = data.getConsoleStrategy();
// strat.subscribeToEvent(CommandData.SUCCESS_EVENT, new CommandEventListener() {
//
// @Override
// public void handleEvent (CommandEvent commandEvent) {
// if (clearOnSuccess) widget.clearCommandLine();
// }
// });
//
// strat.subscribeToEvent(CommandData.FAIL_EVENT, new CommandEventListener() {
//
// @Override
// public void handleEvent (CommandEvent commandEvent) {
// if (clearOnFail) widget.clearCommandLine();
// }
// });

// DefaultKeyMap keyMap = (DefaultKeyMap)getKeyMap();
// keyMap.add(new ShortcutCommand() {
// @Override
// public void execute () {
// data.getConsoleStrategy().execute(widget.getText());
// }
// }, executeKeybind);
//
// keyMap.add( () -> {
// widget.clearCommandLine();
// widget.skipCharacter();
// }, new int[] {Keys.FORWARD_DEL});
//
// keyMap.add( () -> {
// widget.moveCursor(-1);
// widget.getTextField().setBlinkTime(100);
// }, new int[] {Keys.LEFT});
//
// keyMap.add( () -> {
// widget.moveCursor(1);
// }, new int[] {Keys.RIGHT});
//
// keyMap.add( () -> {
// widget.setCursor(0);
// }, new int[] {Keys.HOME});
//
// keyMap.add( () -> {
// widget.setCursor(Integer.MAX_VALUE);
// }, new int[] {Keys.END});
	}

	public void setExecuteKeybind (int[] keybind) {
// DefaultKeyMap map = (DefaultKeyMap)getKeyMap();
// if (map.changeKeybind(executeKeybind, keybind)) {
// executeKeybind = keybind;
// }
	}

	public void clearOnSuccess (boolean clear) {
		clearOnSuccess = clear;
	}

	public void clearOnFail (boolean clear) {
		clearOnFail = clear;
	}

	public void setViewVisibilityShortcut (Shortcut shortcut) {
		viewVisibilityShortcut = shortcut;
	}

	public void clearCommandLine () {
		widget.clearCommandLine();
	}

	public String getText () {
		return widget.getTextField().getText();
	}

// @Override
// public void focus () {
// super.focus();
// getView().getConsole().getStage().setKeyboardFocus(widget.getTextField());
// }

	private class Focus extends FocusListener {

		private final ShortcutManagerAlphabeticNumericFilter filter = new ShortcutManagerAlphabeticNumericFilter();
		private final EventListener<GUIConsoleExecutedShortcutEvent> executedCommandListener = new EventListener<GUIConsoleExecutedShortcutEvent>() {
			@Override
			public void handleEvent (GUIConsoleExecutedShortcutEvent shortcutManagerEvent) {
				if (viewVisibilityShortcut != null
					&& shortcutManagerEvent.getKeybindPacked() == viewVisibilityShortcut.getKeybindPacked()) {
					widget.skipCharacter();
				}
			}
		};

		@Override
		public void keyboardFocusChanged (FocusEvent event, Actor actor, boolean focused) {
// GUIConsoleShortcutManager manager = getView().getConsole().getShortcutManager();
//
// if (focused) {
// manager.setKeycodeFilter(filter);
// manager.setExecutedCommandListener(executedCommandListener);
// } else {
// manager.setKeycodeFilter(null);
// manager.setExecutedCommandListener(null);
// }
		}
	}

	public class ShortcutManagerAlphabeticNumericFilter implements ShortcutManagerFilter {
		@Override
		public boolean acceptKeycodeTyped (GUIConsoleExecutedShortcutEvent context, int keycode) {
			if (viewVisibilityShortcut != null && context.getKeybindPacked() == viewVisibilityShortcut.getKeybindPacked()
				|| context.isModifierKeyPressed()) return true;

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

}
