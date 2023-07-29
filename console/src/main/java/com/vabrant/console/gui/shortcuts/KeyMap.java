
package com.vabrant.console.gui.shortcuts;

public interface KeyMap {
	Shortcut getShortcut (int keybindPacked);

	static int[] asArray (int... array) {
		return array;
	}
}
