
package com.vabrant.console.gui.shortcuts;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.utils.IntMap;
import com.vabrant.console.gui.KeyboardScope;

public class DefaultKeyMap implements KeyMap {
	// ========== Guide ==========//
	// 0 = Control
	// 1 = Shift
	// 2 = Alt
	// 3 = key

	private final KeyboardScope defaultScope;
	private final IntMap<Shortcut> shortcuts;

	public DefaultKeyMap (KeyboardScope defaultScope) {
		this.defaultScope = defaultScope;
		shortcuts = new IntMap<>();
	}

	public Shortcut add (ShortcutCommand command, int... keys) {
		return add(defaultScope, command, keys);
	}

	public Shortcut add (KeyboardScope scope, ShortcutCommand command, int... keys) {
		if (command == null) {
			throw new IllegalArgumentException("Command can't not be null.");
		}

		if (keys == null || keys.length == 0) {
			throw new IllegalArgumentException("Keybind is invalid");
		}

		int[] sorted = ShortcutManager.sortKeybind(keys);
		ShortcutManager.isValidKeybind(sorted);

		int packed = ShortcutManager.packKeybindSorted(sorted);

		if (hasKeybind(packed)) {
			throw new InvalidShortcutException("Keybind:" + printKeybind(keys) + " already exists");
		}

		Shortcut shortcut = new Shortcut();
		shortcut.setKeybindPacked(packed);
		shortcut.setKeybind(sorted);
		shortcut.setConsoleCommand(command);
		shortcut.setScope(scope);

		shortcuts.put(packed, shortcut);
		return shortcut;
	}

	public boolean hasKeybind (int[] keybind) {
		int[] sorted = ShortcutManager.sortKeybind(keybind);
		ShortcutManager.isValidKeybind(keybind);
		return hasKeybind(ShortcutManager.packKeybindSorted(sorted));
	}

	public boolean hasKeybind (int packed) {
		return shortcuts.containsKey(packed);
	}

	public boolean removeKeybind (int[] keybind) {
		return removeKeybind(ShortcutManager.packKeybindUnsorted(keybind));
	}

	public boolean removeKeybind (int packed) {
		return shortcuts.remove(packed) != null;
	}

	public boolean changeKeybind (int oldPacked, int newPacked) {
		return changeKeybind(oldPacked, ShortcutManager.unpackKeybind(newPacked));
	}

	public boolean changeKeybind (int[] oldKeybind, int[] newKeybind) {
		return changeKeybind(packKeybind(oldKeybind), newKeybind);
	}

	public boolean changeKeybind (int[] oldKeybind, int newPacked) {
		return changeKeybind(packKeybind(oldKeybind), newPacked);
	}

	public boolean changeConsoleCommand (int[] keybind, ShortcutCommand command) {
		return changeConsoleCommand(packKeybind(keybind), command);
	}

	public boolean changeConsoleCommand (int packed, ShortcutCommand command) {
		Shortcut s = getShortcut(packed);

		if (s == null) return false;

		s.setConsoleCommand(command);
		return true;
	}

	public boolean changeKeybind (int oldPacked, int[] newKeybind) {
		Shortcut s = getShortcut(oldPacked);

		if (s == null) return false;

		removeKeybind(oldPacked);

		int[] sortedKeybind = ShortcutManager.sortKeybind(newKeybind);
		int packed = packKeybind(sortedKeybind);

		if (shortcuts.containsKey(packed)) return false;

		s.setKeybind(sortedKeybind);

		shortcuts.put(packed, s);
		return true;
	}

	public Shortcut getShortcut (int packed) {
		return shortcuts.get(packed);
	}

	public Shortcut getShortcut (int... keybind) {
		ShortcutManager.isValidKeybind(keybind);
		int packed = GUIConsoleShortcutManager.packKeybindUnsorted(keybind);
		return shortcuts.get(packed);
	}

	private int packKeybind (int[] sortedKeybind) {
		ShortcutManager.isValidKeybind(sortedKeybind);
		return ShortcutManager.packKeybindSorted(sortedKeybind);
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
