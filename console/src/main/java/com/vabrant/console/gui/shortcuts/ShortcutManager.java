
package com.vabrant.console.gui.shortcuts;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.utils.IntArray;

import java.util.Arrays;

public class ShortcutManager {
	// ========== Guide ==========//
	// 0 = Control
	// 1 = Shift
	// 2 = Alt
	// 3 = key

	public static final int MAX_KEYS = 4;

	protected int[] pressedKeys;
	protected int pressedKeysPacked;
	protected KeyMap keyMap;

	public ShortcutManager () {
		pressedKeys = new int[MAX_KEYS];
	}

	public void setKeyMap (KeyMap keyMap) {
		this.keyMap = keyMap;
	}

	public KeyMap getKeyMap () {
		return keyMap;
	}

	public void clearPressedKeys () {
		Arrays.fill(pressedKeys, 0);
		pressedKeysPacked = 0;
	}

	public boolean keyUp (int keycode) {
		if (keyMap == null) return false;

		boolean dirty = clearKey(keycode);
		if (dirty) pressedKeysPacked = packKeybindSorted(pressedKeys);
		return false;
	}

	public boolean keyDown (int keycode) {
		if (keyMap == null || pressedKeys[3] != 0) return false;

		boolean dirty = setKey(pressedKeys, keycode);

		if (dirty) pressedKeysPacked = packKeybindSorted(pressedKeys);

		Shortcut shortcut = keyMap.getShortcut(pressedKeysPacked);

		if (shortcut == null) return false;

		shortcut.getConsoleCommand().run();

		return true;
	}

	// Returns whether to repack or not
	protected boolean clearKey (int keycode) {
		switch (keycode) {
		case Keys.CONTROL_LEFT:
		case Keys.CONTROL_RIGHT:
			if (pressedKeys[0] > 0) {
				pressedKeys[0] = 0;
				return true;
			}
			break;
		case Keys.SHIFT_LEFT:
		case Keys.SHIFT_RIGHT:
			if (pressedKeys[1] > 0) {
				pressedKeys[1] = 0;
				return true;
			}
			break;
		case Keys.ALT_LEFT:
		case Keys.ALT_RIGHT:
		case Keys.SYM:
			if (pressedKeys[2] > 0) {
				pressedKeys[2] = 0;
				return true;
			}
			break;
		default:
			if (pressedKeys[3] > 0) {
				pressedKeys[3] = 0;
				return true;
			}
			break;
		}

		return false;
	}

	public static boolean setKey (int[] keybind, int keycode) {
		boolean dirty = false;

		if (keycode == 0) return dirty;

		switch (keycode) {
		case Keys.CONTROL_LEFT:
		case Keys.CONTROL_RIGHT:
			if (keybind[0] == 0) {
				keybind[0] = Keys.CONTROL_LEFT;
				dirty = true;
			}
			break;
		case Keys.SHIFT_LEFT:
		case Keys.SHIFT_RIGHT:
			if (keybind[1] == 0) {
				keybind[1] = Keys.SHIFT_LEFT;
				dirty = true;
			}
			break;
		case Keys.ALT_LEFT:
		case Keys.ALT_RIGHT:
		case Keys.SYM:
			if (keybind[2] == 0) {
				keybind[2] = Keys.ALT_LEFT;
				dirty = true;
			}
			break;
		default:
			if (keybind[3] == 0) {
				keybind[3] = keycode;
				dirty = true;
			}
			break;
		}

		return dirty;
	}

	public static int[] sortKeybind (int[] keybind) {
		int[] helper = new int[MAX_KEYS];
		for (int i : keybind) {
			setKey(helper, i);
		}
		return helper;
	}

	public static int[] unpackKeybind (int packed) {
		IntArray arr = new IntArray();

		for (int i = 0; i < MAX_KEYS; i++) {
			int key = (packed >> (i << 3)) & 0xFF;

			if (key <= 0) continue;

			arr.add(key);
		}
		return arr.toArray();
	}

	public static int packKeybindUnsorted (int[] keybind) {
		return packKeybindSorted(sortKeybind(keybind));
	}

	public static int packKeybindSorted (int[] keybind) {
		int idx = 0;
		int packed = 0;
		for (int i = 0; i < keybind.length; i++) {
			if (keybind[i] == 0) continue;
			packed |= (keybind[i] & 0xFF) << (idx++ << 3);
		}
		return packed;
	}

	// Only modifiers is invalid
	// Empty is invalid
	// Using a restricted keybind is invalid
	// Only one non modifier key is allowed
	public static void isValidKeybind (int[] keys) {
		if (keys == null || keys.length == 0 || keys.length > ShortcutManager.MAX_KEYS) {
			throw new InvalidShortcutException(
				"Keybind must not be null and have a length between 0 and " + ShortcutManager.MAX_KEYS);
		}

		boolean allModifiers = true;
		boolean hasNormalKey = false;
		boolean hasShift = false;
		boolean hasAlt = false;
		boolean hasControl = false;

		for (int i = 0; i < keys.length; i++) {
			if (keys[i] <= 0) continue;

			// Treat left and right modifier keys the same
			switch (keys[i]) {
			case Keys.ALT_LEFT:
			case Keys.ALT_RIGHT:
			case Keys.SYM:
				if (hasAlt) throw new InvalidShortcutException("Alt key already added.");
				hasAlt = true;
				break;
			case Keys.CONTROL_LEFT:
			case Keys.CONTROL_RIGHT:
				if (hasControl) throw new InvalidShortcutException("Control key already added.");
				hasControl = true;
				break;
			case Keys.SHIFT_LEFT:
			case Keys.SHIFT_RIGHT:
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

	public static boolean isModifierKeyPressed (int[] keybind) {
		if (keybind.length > ShortcutManager.MAX_KEYS) return false;

		if (keybind.length != ShortcutManager.MAX_KEYS) {
			keybind = ShortcutManager.sortKeybind(keybind);
		}

		return keybind[0] != 0 || keybind[1] != 0 || keybind[2] != 0;
	}

	public static boolean isControlPressed (int[] keybind) {
		if (keybind.length > ShortcutManager.MAX_KEYS) return false;

		if (keybind.length != ShortcutManager.MAX_KEYS) {
			keybind = ShortcutManager.sortKeybind(keybind);
		}

		return keybind[0] != 0;
	}

	public static boolean isShiftPressed (int[] keybind) {
		if (keybind.length > ShortcutManager.MAX_KEYS) return false;

		if (keybind.length != ShortcutManager.MAX_KEYS) {
			keybind = ShortcutManager.sortKeybind(keybind);
		}

		return keybind[1] != 0;
	}

	public static boolean isAltSymPressed (int[] keybind) {
		if (keybind.length > ShortcutManager.MAX_KEYS) return false;

		if (keybind.length != ShortcutManager.MAX_KEYS) {
			keybind = ShortcutManager.sortKeybind(keybind);
		}

		return keybind[2] != 0;
	}

}
