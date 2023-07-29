
package com.vabrant.console.gui.shortcuts;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.vabrant.console.EventListener;
import com.vabrant.console.EventManager;
import com.vabrant.console.gui.ConsoleScope;
import com.vabrant.console.gui.GUIConsole;

import java.util.Arrays;

public class ShortcutManager extends InputAdapter {
	// ========== Guide ==========//
	// 0 = Control
	// 1 = Shift
	// 2 = Alt
	// 3 = key

	public static final ConsoleScope GLOBAL_SCOPE = new ConsoleScope("global");

	public static final String EXECUTED_EVENT = "executed";
	public static final int MAX_KEYS = 4;

	private boolean dirty;
	private int currentlyPressedKeysPacked;
	private final int[] pressedKeys;
	private GUIConsole console;
	private final EventManager eventManager;
	private KeyMap keyMap;
	private ShortcutManagerFilter filter;
	private EventListener<ShortcutManagerEvent> executedCommandListener;
	private ShortcutManagerEvent shortcutManagerEvent;

	public ShortcutManager () {
		pressedKeys = new int[MAX_KEYS];
		eventManager = new EventManager(EXECUTED_EVENT);
		shortcutManagerEvent = new ShortcutManagerEvent();
	}

	public void subscribeToEvent (String event, EventListener<ShortcutManagerEvent> listener) {
		eventManager.subscribe(event, listener);
	}

	public void subscribeToExecutedEvent (EventListener<ShortcutManagerEvent> listener) {
		eventManager.subscribe(EXECUTED_EVENT, listener);
	}

	public void unsubscribeFromExecutedEvent (EventListener<ShortcutManagerEvent> listener) {
		eventManager.unsubscribe(EXECUTED_EVENT, listener);
	}

	public void setGUIConsole (GUIConsole console) {
		this.console = console;
	}

	public void setKeyMap (KeyMap keyMap) {
		this.keyMap = keyMap;
	}

	public int getCurrentlyPressedKeysPacked () {
		return currentlyPressedKeysPacked;
	}

	public void setKeycodeFilter (ShortcutManagerFilter filter) {
		this.filter = filter;
	}

	public void setExecutedCommandListener (EventListener<ShortcutManagerEvent> listener) {
		executedCommandListener = listener;
	}

	public static boolean setKey (int[] keys, int keycode) {
		boolean dirty = false;

		if (keycode == 0) return dirty;

		switch (keycode) {
		case Input.Keys.CONTROL_LEFT:
		case Input.Keys.CONTROL_RIGHT:
			if (keys[0] == 0) {
				keys[0] = Input.Keys.CONTROL_LEFT;
				dirty = true;
			}
			break;
		case Input.Keys.SHIFT_LEFT:
		case Input.Keys.SHIFT_RIGHT:
			if (keys[1] == 0) {
				keys[1] = Input.Keys.SHIFT_LEFT;
				dirty = true;
			}
			break;
		case Input.Keys.ALT_LEFT:
		case Input.Keys.ALT_RIGHT:
			if (keys[2] == 0) {
				keys[2] = Input.Keys.ALT_LEFT;
				dirty = true;
			}
			break;
		default:
			if (keys[3] == 0) {
				keys[3] = keycode;
				dirty = true;
			}
			break;
		}

		return dirty;
	}

	private void clearKey (int keycode) {
		switch (keycode) {
		case Input.Keys.CONTROL_LEFT:
		case Input.Keys.CONTROL_RIGHT:
			if (pressedKeys[0] > 0) {
				pressedKeys[0] = 0;
				dirty = true;
			}
			break;
		case Input.Keys.SHIFT_LEFT:
		case Input.Keys.SHIFT_RIGHT:
			if (pressedKeys[1] > 0) {
				pressedKeys[1] = 0;
				dirty = true;
			}
			break;
		case Input.Keys.ALT_LEFT:
		case Input.Keys.ALT_RIGHT:
			if (pressedKeys[2] > 0) {
				pressedKeys[2] = 0;
				dirty = true;
			}
			break;
		default:
			if (pressedKeys[3] > 0) {
				pressedKeys[3] = 0;
				dirty = true;
			}
			break;
		}
	}

	@Override
	public boolean keyDown (int keycode) {
		// Modifier keys have to be pressed before the normal key
		// ctrl -> shift -> o != o -> ctrl -> shift

		if (pressedKeys[3] != 0 || keyMap == null) return false;

		dirty = setKey(pressedKeys, keycode);

		pack();

		if (filter != null) {
			shortcutManagerEvent.clear().setKeybind(pressedKeys).setPackedKeybind(currentlyPressedKeysPacked)
				.setConsoleScope(console.getScope());
			if (!filter.acceptKeycodeTyped(shortcutManagerEvent, keycode)) return false;
		}

		Shortcut shortcut = keyMap.getShortcut(currentlyPressedKeysPacked);

		if (shortcut == null) return false;

		ConsoleScope scope = shortcut.getScope();

		if (!scope.equals(GLOBAL_SCOPE) && !scope.equals(console.getScope())) return false;

		shortcut.getConsoleCommand().execute();

		ShortcutManagerEvent context = new ShortcutManagerEvent();
		context.setKeybind(pressedKeys);
		context.setPackedKeybind(currentlyPressedKeysPacked);
		if (executedCommandListener != null) executedCommandListener.handleEvent(context);
		eventManager.fire(EXECUTED_EVENT, context);
		return true;
	}

	@Override
	public boolean keyUp (int keycode) {
		if (keyMap == null) return false;
		clearKey(keycode);
		pack();
		return false;
	}

	public static int[] unpack (int[] keybind, int packed) {
		for (int i = 0; i < MAX_KEYS; i++) {
			keybind[i] = (packed >> (i << 3)) & 0xFF;
		}
		return keybind;
	}

	public static int packKeys (int[] keys) {
		int idx = 0;
		int packed = 0;
		for (int i = 0; i < keys.length; i++) {
			if (keys[i] == 0) continue;
			packed |= (keys[i] & 0xFF) << (idx++ << 3);
		}
		return packed;
	}

	private void pack () {
		if (!dirty) return;
		dirty = false;
		currentlyPressedKeysPacked = packKeys(pressedKeys);
	}

	public interface ShortcutManagerFilter {
		boolean acceptKeycodeTyped (ShortcutManagerEvent context, int keycode);
	}

	public static class ShortcutManagerEvent {

		private int[] keybind;
		private int keybindPacked;
		private ConsoleScope consoleScope;
		private Shortcut shortcut;

		ShortcutManagerEvent () {
			keybind = new int[MAX_KEYS];
		}

		ShortcutManagerEvent setKeybind (int[] keybind) {
			for (int i = 0; i < MAX_KEYS; i++) {
				this.keybind[i] = keybind[i];
			}
			return this;
		}

		public int[] getKeybind () {
			return keybind;
		}

		ShortcutManagerEvent setPackedKeybind (int keybindPacked) {
			this.keybindPacked = keybindPacked;
			return this;
		}

		public int getKeybindPacked () {
			return keybindPacked;
		}

		ShortcutManagerEvent setConsoleScope (ConsoleScope scope) {
			consoleScope = scope;
			return this;
		}

		public ConsoleScope getConsoleScope () {
			return consoleScope;
		}

		public boolean isModifierKeyPressed () {
			return keybind[0] != 0 || keybind[1] != 0 || keybind[2] != 0;
		}

		ShortcutManagerEvent setShortcut (Shortcut shortcut) {
			this.shortcut = shortcut;
			return this;
		}

		public Shortcut getShortcut () {
			return shortcut;
		}

		ShortcutManagerEvent clear () {
			Arrays.fill(keybind, 0);
			keybindPacked = 0;
			consoleScope = null;
			shortcut = null;
			return this;
		}

	}

}
