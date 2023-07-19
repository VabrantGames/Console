
package com.vabrant.console.gui;

import com.vabrant.console.gui.shortcuts.KeyMap;

public interface FocusObject {
	void focus ();

	void unfocus ();

	ConsoleScope getScope ();

	<T extends KeyMap> T getKeyMap ();

	boolean lockFocus ();

	String getName ();
}
