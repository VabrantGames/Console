
package com.vabrant.console.gui;

import com.vabrant.console.gui.shortcuts.KeyMap;

public class ParentFocusObject implements FocusObject {

	private final FocusObject parent;
	private FocusObject child;

	public ParentFocusObject (FocusObject parent) {
		this(parent, null);
	}

	public ParentFocusObject (FocusObject parent, FocusObject child) {
		this.parent = parent;
		this.child = child;
	}

	public void setChild (FocusObject child) {
		this.child = child;
	}

	@Override
	public void focus () {
		parent.focus();
		if (child != null) child.focus();
	}

	@Override
	public void unfocus () {

	}

	@Override
	public DefaultKeyboardScope getKeyboardScope () {
		return null;
	}

	@Override
	public KeyMap getKeyMap () {
		return null;
	}

	@Override
	public boolean lockFocus () {
		return false;
	}

	@Override
	public String getName () {
		return null;
	}
}
