
package com.vabrant.console.gui.shortcuts;

public interface KeyMap {
	Shortcut register (String ID, Runnable command, int... keybind);

	Shortcut getShortcut (int packed);
}
