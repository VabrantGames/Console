
package com.vabrant.console.gui;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.vabrant.console.ConsoleCommand;
import com.vabrant.console.EventListener;
import com.vabrant.console.EventManager;
import com.vabrant.console.exceptions.InvalidShortcutException;

import java.util.Arrays;

public class ShortcutManager extends InputAdapter {

	public static final String GLOBAL_SCOPE = "global";
	public static final String EXECUTED_EVENT = "executed";
	public static final int MAX_KEYS = 4;

	private boolean dirty;
	private int currentlyPressedKeysPacked;
	private final int[] packHelper;
	private final int[] pressedKeys;
// private final IntMap<ShortcutContext> shortcuts;
	private GUIConsole console;
	private final EventManager eventManager;
// private Array<KeyMap> keyMaps;
	private KeyMap consoleKeyMap;
	private KeyMap cacheKeyMap;
	private KeyMap panelKeyMap;
	private ShortcutManagerFilter filter;
	private EventListener<ShortcutManagerContext> executedCommandListener;
	private ShortcutManagerContext shortcutManagerContext;

	public ShortcutManager () {
// shortcuts = new IntMap<>();
		pressedKeys = new int[MAX_KEYS];
		packHelper = new int[MAX_KEYS];
		eventManager = new EventManager(EXECUTED_EVENT);
		shortcutManagerContext = new ShortcutManagerContext();
// keyMaps = new Array<>();
	}

	public void subscribeToExecutedEvent (EventListener<ShortcutManagerContext> listener) {
		eventManager.subscribe(EXECUTED_EVENT, listener);
	}

	public void unsubscribeFromExecutedEvent (EventListener<ShortcutManagerContext> listener) {
		eventManager.unsubscribe(EXECUTED_EVENT, listener);
	}

	void setGUIConsole (GUIConsole console) {
		this.console = console;
	}

// void insertKeyMap(KeyMap keyMap, int pos) {
//// keyMaps.insert(pos, keyMap);
// }
//
// public void addKeyMap(KeyMap keyMap) {
//// keyMaps.add(keyMap);
// }

	void setConsoleKeyMap (KeyMap keyMap) {
		consoleKeyMap = keyMap;
	}

	void setCacheKeyMap (KeyMap keyMap) {
		consoleKeyMap = keyMap;
	}

	void setPanelKeyMap (KeyMap keyMap) {
		panelKeyMap = keyMap;
	}

	public int getCurrentlyPressedKeysPacked () {
		return currentlyPressedKeysPacked;
	}

	public void setKeycodeFilter (ShortcutManagerFilter filter) {
		this.filter = filter;
	}

	public void setExecutedCommandListener (EventListener<ShortcutManagerContext> listener) {
		executedCommandListener = listener;
	}

// public int add (int[] keys, ConsoleCommand command) {
// return add(keys, command, ConsoleScope.DEFAULT);
// }

// public int add (int[] keys, ConsoleCommand command, ConsoleScope scope) {
// if (command == null) throw new IllegalArgumentException("Command con not be null.");
//
// isValidKeybind(keys);
//
// Arrays.fill(packHelper, 0);
// for (int i : keys) {
// setKey(packHelper, i);
// }
//
// dirty = true;
//
// outer:
// if (scope.equals(ConsoleScope.GLOBAL)) {
// String s = Input.Keys.toString(packHelper[3]);
// if (s.length() == 1) {
// char k = s.charAt(0);
// boolean isCharOrNum = Character.isLetter(k) || Character.isDigit(k);
// if (isCharOrNum) {
// // Global shortcuts that use characters or digits need a modifier key
// for (int i = 0; i < MAX_KEYS - 1; i++) {
// if (packHelper[i] > 0) break outer;
// }
// throw new InvalidShortcutException(
// "Shortcuts with a single alphabetic or numerical keybind, must include a modifier key.");
// }
// }
// }
//
// int packed = packKeys(packHelper);
// shortcuts.put(packed, new ShortcutContext(scope, command));
// return packed;
// }

	/** Replaces the keybind of an existing command.
	 *
	 * @param oldKeybind
	 * @param newKeybind
	 * @return newKeybind if oldKeybind exists, otherwise oldKeybind */
	public int replace (int oldKeybind, int[] newKeybind) {
// ConsoleCommand command = shortcuts.remove(oldKeybind).getCommand();
// if (command == null) return oldKeybind;
// return add(newKeybind, command);
		return 0;
	}

	public boolean contains (int packedKeybind) {
// return shortcuts.containsKey(packedKeybind);
		return false;
	}

	public ConsoleCommand remove (int packedKeybind) {
// return shortcuts.remove(packedKeybind).getCommand();
		return null;
	}

	// Only modifiers is invalid
	// Empty is invalid
	// Using a restricted keybind is invalid
	// Only one non modifier key is allowed
	private void isValidKeybind (int[] keys) {
		if (keys == null || keys.length == 0 || keys.length > MAX_KEYS) {
			throw new InvalidShortcutException("Keybind must not be null and have a length between 0 and " + MAX_KEYS);
		}

		boolean allModifiers = true;
		boolean hasNormalKey = false;
		boolean hasShift = false;
		boolean hasAlt = false;
		boolean hasControl = false;

		for (int i = 0; i < keys.length; i++) {
			// Treat left and right modifier keys the same
			switch (keys[i]) {
			case Input.Keys.ALT_LEFT:
			case Input.Keys.ALT_RIGHT:
				if (hasAlt) throw new InvalidShortcutException("Alt key already added.");
				hasAlt = true;
				break;
			case Input.Keys.CONTROL_LEFT:
			case Input.Keys.CONTROL_RIGHT:
				if (hasControl) throw new InvalidShortcutException("Control key already added.");
				hasControl = true;
				break;
			case Input.Keys.SHIFT_LEFT:
			case Input.Keys.SHIFT_RIGHT:
				if (hasShift) throw new InvalidShortcutException("Shift key already added.");
				hasShift = true;
				break;
			default:
				if (hasNormalKey) throw new InvalidShortcutException("Keybind must have a maximum of 1 non modifier key");

				hasNormalKey = true;
				allModifiers = false;
			}
		}

		if (allModifiers) throw new InvalidShortcutException("All modifier keys are not allowed.");
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
		if (pressedKeys[3] != 0) return false;

		dirty = setKey(pressedKeys, keycode);
// dirty = true;
		pack();

		if (filter != null) {
			shortcutManagerContext.clear().setKeybind(pressedKeys).setPackedKeybind(currentlyPressedKeysPacked)
				.setConsoleScope(console.getScope());
			if (!filter.acceptKeycodeTyped(shortcutManagerContext, keycode)) return true;
		}

		Shortcut shortcut = consoleKeyMap.getShortcut(currentlyPressedKeysPacked);

		outer:
		if (shortcut == null) {
			if (cacheKeyMap != null) {
				shortcut = cacheKeyMap.getShortcut(currentlyPressedKeysPacked);
				if (shortcut != null) break outer;
			}

			if (panelKeyMap != null) {
				shortcut = panelKeyMap.getShortcut(currentlyPressedKeysPacked);
				if (shortcut != null) break outer;
			}

			return false;
		}

		String scope = shortcut.getScope();

		if (scope.equals(GLOBAL_SCOPE) || scope.equals(console.getScope())) {
			shortcut.getConsoleCommand().execute();
		}

		ShortcutManagerContext context = new ShortcutManagerContext();
		context.setKeybind(pressedKeys);
		context.setPackedKeybind(currentlyPressedKeysPacked);
		if (executedCommandListener != null) executedCommandListener.handleEvent(context);
		eventManager.fire(EXECUTED_EVENT, context);
		return true;
	}

	@Override
	public boolean keyUp (int keycode) {
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
		boolean acceptKeycodeTyped (ShortcutManagerContext context, int keycode);
	}

	public static class ShortcutManagerContext {

		private int[] keybind;
		private int keybindPacked;
		private String consoleScope;
		private Shortcut shortcut;

		ShortcutManagerContext () {
			keybind = new int[MAX_KEYS];
		}

		ShortcutManagerContext setKeybind (int[] keybind) {
			for (int i = 0; i < MAX_KEYS; i++) {
				this.keybind[i] = keybind[i];
			}
			return this;
		}

		public int[] getKeybind () {
			return keybind;
		}

		ShortcutManagerContext setPackedKeybind (int keybindPacked) {
			this.keybindPacked = keybindPacked;
			return this;
		}

		public int getKeybindPacked () {
			return keybindPacked;
		}

		ShortcutManagerContext setConsoleScope (String scope) {
			consoleScope = scope;
			return this;
		}

		public String getConsoleScope () {
			return consoleScope;
		}

		ShortcutManagerContext setShortcut (Shortcut shortcut) {
			this.shortcut = shortcut;
			return this;
		}

		public Shortcut getShortcut () {
			return shortcut;
		}

		ShortcutManagerContext clear () {
			Arrays.fill(keybind, 0);
			keybindPacked = 0;
			consoleScope = "";
			shortcut = null;
			return this;
		}

	}

}
