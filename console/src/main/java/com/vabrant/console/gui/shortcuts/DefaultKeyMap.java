
package com.vabrant.console.gui.shortcuts;

import com.badlogic.gdx.utils.ObjectMap;
import com.vabrant.console.KeyboardScope;
import com.vabrant.console.Utils;

public class DefaultKeyMap implements KeyMap {
	// ========== Guide ==========//
	// 0 = Control
	// 1 = Shift
	// 2 = Alt
	// 3 = key

	private KeyboardScope defaultScope;
	private ObjectMap<String, Shortcut> shortcuts;

	public DefaultKeyMap (KeyboardScope defaultScope) {
		this.defaultScope = defaultScope;
		shortcuts = new ObjectMap<>(6);
	}

	@Override
	public Shortcut register (String ID, Runnable command, int... keybind) {
		return register(ID, defaultScope, command, null, keybind);
	}

	public Shortcut register (String ID, KeyboardScope scope, Runnable command, int... keybind) {
		return register(ID, scope, command, null, keybind);
	}

	public Shortcut register (String ID, KeyboardScope scope, Runnable command, String description, int... keybind) {
		if (shortcuts.containsKey(ID)) throw new IllegalArgumentException("Shortcut with name '" + ID + "' already registered");

		Shortcut s = new Shortcut();

		if (keybind != null) {
			if (hasKeybind(keybind)) {
				throw new InvalidShortcutException("Keybind:" + Utils.printKeybind(keybind) + " already in use");
			}

			int[] sorted = ShortcutManager.sortKeybind(keybind);
			s.setKeybind(sorted);
			s.setKeybindPacked(ShortcutManager.packKeybindSorted(sorted));
		}

		s.setConsoleCommand(command);
		s.setScope(scope);
		s.setDescription(description);

		shortcuts.put(ID, s);

		return s;
	}

	public boolean unregister (String ID) {
		return shortcuts.remove(ID) != null;
	}

	public boolean hasKeybind (int[] keybind) {
		int[] sorted = ShortcutManager.sortKeybind(keybind);
		ShortcutManager.isValidKeybind(keybind);
		return hasKeybind(ShortcutManager.packKeybindSorted(sorted));
	}

	public boolean hasKeybind (int packed) {
		for (Shortcut s : shortcuts.values()) {
			if (s.getKeybindPacked() == packed) {
				return true;
			}
		}
		return false;
	}

	public boolean changeKeybind (String ID, int... keybind) {
		Shortcut s = getShortcut(ID);

		if (s == null) return false;

		if (keybind != null) {
			if (hasKeybind(keybind)) {
				throw new InvalidShortcutException("Keybind:" + Utils.printKeybind(keybind) + " already in use");
			}

			int[] sorted = ShortcutManager.sortKeybind(keybind);
			s.setKeybind(sorted);
			s.setKeybindPacked(ShortcutManager.packKeybindSorted(sorted));
		} else {
			s.setKeybind(null);
			s.setKeybindPacked(0);
		}

		return true;
	}

	public boolean changeConsoleCommand (String ID, Runnable command) {
		Shortcut s = getShortcut(ID);

		if (s == null) return false;

		s.setConsoleCommand(command);
		return true;
	}

	public boolean hasShortcut (String ID) {
		return getShortcut(ID) != null;
	}

	public Shortcut getShortcut (int packed) {
		for (Shortcut s : shortcuts.values()) {
			if (s.getKeybindPacked() == packed) {
				return s;
			}
		}
		return null;
	}

	public Shortcut getShortcut (String ID) {
		return shortcuts.get(ID);
	}

	private int packKeybind (int[] sortedKeybind) {
		ShortcutManager.isValidKeybind(sortedKeybind);
		return ShortcutManager.packKeybindSorted(sortedKeybind);
	}

}
