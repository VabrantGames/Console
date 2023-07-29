
package com.vabrant.console.gui;

import com.vabrant.console.gui.shortcuts.KeyMap;

public interface FocusObject<T extends KeyMap> {
	void focus ();

	void unfocus ();

	ConsoleScope getScope ();

	T getKeyMap ();

	boolean lockFocus ();

	String getName ();
}
