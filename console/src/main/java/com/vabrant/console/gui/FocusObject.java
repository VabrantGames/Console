package com.vabrant.console.gui;

import com.vabrant.console.gui.shortcuts.KeyMap;

public interface FocusObject {
	void focus();
	void unfocus();
	ConsoleScope getScope();
	KeyMap getKeyMap();
	boolean lockFocus();
	String getName();
}
