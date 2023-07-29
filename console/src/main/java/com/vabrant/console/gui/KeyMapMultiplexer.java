
package com.vabrant.console.gui;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.SnapshotArray;
import com.vabrant.console.gui.shortcuts.KeyMap;
import com.vabrant.console.gui.shortcuts.Shortcut;

public class KeyMapMultiplexer implements KeyMap {

	private final SnapshotArray<KeyMap> keyMaps;

	public KeyMapMultiplexer () {
		keyMaps = new SnapshotArray<>(false, 4, KeyMap.class);
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

	public void insert (int idx, KeyMap keyMap) {
		keyMaps.insert(idx, keyMap);
	}

	public int indexOf (KeyMap keyMap) {
		return keyMaps.indexOf(keyMap, false);
	}

	public boolean remove (KeyMap keyMap) {
		if (keyMap == null) return false;
		return keyMaps.removeValue(keyMap, false);
	}

	@Override
	public Shortcut getShortcut (int keybindPacked) {
		Shortcut shortcut = null;

		KeyMap[] maps = keyMaps.begin();
		try {
			for (int i = 0, n = keyMaps.size; i < n; i++) {
				shortcut = maps[i].getShortcut(keybindPacked);

				if (shortcut != null) {
					return shortcut;
				}
			}
		} finally {
			keyMaps.end();
		}
		return null;
	}
}
