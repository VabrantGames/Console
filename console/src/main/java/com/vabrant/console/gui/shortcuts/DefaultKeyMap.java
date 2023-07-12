
package com.vabrant.console.gui.shortcuts;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.utils.IntMap;
import com.vabrant.console.gui.ConsoleScope;
import com.vabrant.console.exceptions.InvalidShortcutException;

import java.util.Arrays;

public class DefaultKeyMap implements KeyMap {
	// ========== Guide ==========//
	// 0 = Control
	// 1 = Shift
	// 2 = Alt
	// 3 = key

	private final int[] packHelper;
	private final ConsoleScope scope;
	private final IntMap<Shortcut> shortcuts;

	public DefaultKeyMap (ConsoleScope scope) {
		this.scope = scope;
		shortcuts = new IntMap<>();
		packHelper = new int[ShortcutManager.MAX_KEYS];
	}

	public Shortcut add (ConsoleCommand command, int... keys) {
		if (command == null) {
			throw new IllegalArgumentException("Command con not be null.");
		}

		if (keys == null) {
			throw new IllegalArgumentException("Keybind is null");
		}

		isValidKeybind(keys);
		setupPackHelper(keys);

		int packed = ShortcutManager.packKeys(packHelper);

		if (hasKeybind(packed)) {
			throw new InvalidShortcutException("Keybind:" + printKeybind(keys) + " already exists");
		}

		Shortcut shortcut = new Shortcut();
		shortcut.setKeybindPacked(packed);
		shortcut.setKeybind(packHelper);
		shortcut.setConsoleCommand(command);
		shortcut.setScope(scope);

		shortcuts.put(packed, shortcut);
		return shortcut;
	}

	public boolean hasKeybind (int[] keybind) {
		isValidKeybind(keybind);
		setupPackHelper(keybind);
		int packed = ShortcutManager.packKeys(packHelper);
		return hasKeybind(packed);
	}

	public boolean hasKeybind (int packed) {
		return shortcuts.containsKey(packed);
	}

	public boolean removeKeybind (int[] keybind) {
		return removeKeybind(packKeybind(keybind));
	}

	public boolean removeKeybind (int packed) {
		return shortcuts.remove(packed) != null;
	}

	public boolean changeKeybind (int oldPacked, int newPacked) {
		return changeKeybind(oldPacked, ShortcutManager.unpack(new int[ShortcutManager.MAX_KEYS], newPacked));
	}

	public boolean changeKeybind (int[] oldKeybind, int[] newKeybind) {
		return changeKeybind(packKeybind(oldKeybind), newKeybind);
	}

	public boolean changeKeybind (int[] oldKeybind, int newPacked) {
		return changeKeybind(packKeybind(oldKeybind), newPacked);
	}

	public boolean changeConsoleCommand (int[] keybind, ConsoleCommand command) {
		return changeConsoleCommand(packKeybind(keybind), command);
	}

	public boolean changeConsoleCommand (int packed, ConsoleCommand command) {
		Shortcut s = getShortcut(packed);

		if (s == null) return false;

		s.setConsoleCommand(command);
		return true;
	}

	public boolean changeKeybind (int oldPacked, int[] newKeybind) {
		Shortcut s = getShortcut(oldPacked);

		if (s == null) return false;

		removeKeybind(oldPacked);

		int packed = packKeybind(newKeybind);
		s.setKeybind(packHelper);

		shortcuts.put(packed, s);
		return true;
	}

	@Override
	public Shortcut getShortcut (int packed) {
		return shortcuts.get(packed);
	}

	public Shortcut getShortcut (int... keybind) {
		isValidKeybind(keybind);
		setupPackHelper(keybind);
		int packed = ShortcutManager.packKeys(packHelper);
		return shortcuts.get(packed);
	}

	private int packKeybind (int[] keybind) {
		isValidKeybind(keybind);
		setupPackHelper(keybind);
		return ShortcutManager.packKeys(packHelper);
	}

	private void setupPackHelper (int[] keys) {
		Arrays.fill(packHelper, 0);
		for (int i : keys) {
			ShortcutManager.setKey(packHelper, i);
		}
	}

	// Only modifiers is invalid
	// Empty is invalid
	// Using a restricted keybind is invalid
	// Only one non modifier key is allowed
	private void isValidKeybind (int[] keys) {
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
			// Treat left and right modifier keys the same
			switch (keys[i]) {
			case Keys.ALT_LEFT:
			case Keys.ALT_RIGHT:
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

	private String printKeybind (int[] keybind) {
		StringBuilder builder = new StringBuilder();
		builder.append('[');

		for (int i = 0; i < keybind.length; i++) {

			switch (keybind[i]) {
			case Keys.GRAVE:
				builder.append("Grave");
				break;
			default:
				builder.append(Keys.toString(keybind[i]));
				break;
			}

			if (i < keybind.length - 1) builder.append(" ,");
		}

		builder.append("]");
		return builder.toString();
	}
}
