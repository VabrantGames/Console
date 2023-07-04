
package com.vabrant.console.gui;

import com.vabrant.console.ConsoleCache;
import com.vabrant.console.ConsoleCommand;

public class GUIConsoleCache extends ConsoleCache {

//	private ShortcutManager shortcutManager;
	private KeyMap keyMap;

	public GUIConsoleCache () {
//		shortcutManager = new ShortcutManager();
		keyMap = new KeyMap("");
	}

	public KeyMap getKeyMap() {
		return keyMap;
	}

	public ShortcutManager getShortcutManager () {
		return null;
//		return shortcutManager;
	}

	public int addShortcut(ConsoleCommand command, int... keybind) {
		return keyMap.add(command, keybind);
	}

	public int addShortcut (int[] keybind, ConsoleCommand command) {
//		return shortcutManager.add(keybind, command);
		return 0;
	}

	public int addShortcut (int[] keybind, ConsoleCommand command, ConsoleScope scope) {
//		return shortcutManager.add(keybind, command, scope);
		return 0;
	}
}
