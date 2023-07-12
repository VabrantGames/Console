
package com.vabrant.console.gui;

import com.badlogic.gdx.utils.Array;
import com.vabrant.console.gui.shortcuts.KeyMap;
import com.vabrant.console.gui.shortcuts.Shortcut;

public class KeyMapMultiplexer implements KeyMap {

	private Array<KeyMap> keyMaps;

	public KeyMapMultiplexer () {
		keyMaps = new Array<>();
	}

	public Array<KeyMap> getKeyMaps () {
		return keyMaps;
	}

	public void add (KeyMap keyMap) {
		if (keyMap == null) {
			throw new IllegalArgumentException("KeyMap is null");
		}
		keyMaps.add(keyMap);
	}

	@Override
	public Shortcut getShortcut (int keybindPacked) {
		Shortcut shortcut = null;

		for (KeyMap map : keyMaps) {
			shortcut = map.getShortcut(keybindPacked);

			if (shortcut != null) {
				return shortcut;
			}
		}
		return null;
	}
}
