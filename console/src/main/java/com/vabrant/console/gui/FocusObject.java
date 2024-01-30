
package com.vabrant.console.gui;

import com.vabrant.console.KeyboardScope;
import com.vabrant.console.gui.shortcuts.KeyMap;

public interface FocusObject {
	void focus ();

	void unfocus ();

	KeyboardScope getKeyboardScope ();

	KeyMap getKeyMap ();

	boolean lockFocus ();

	String getName ();
}
