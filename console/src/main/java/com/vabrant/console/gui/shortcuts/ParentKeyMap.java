
package com.vabrant.console.gui.shortcuts;

import com.vabrant.console.ConsoleRuntimeException;

public class ParentKeyMap implements KeyMap {

	private KeyMap parent;
	private KeyMap child;

	public ParentKeyMap () {
	}

	public ParentKeyMap (KeyMap parent) {
		this(parent, null);
	}

	public ParentKeyMap (KeyMap parent, KeyMap child) {
		this.parent = parent;
		this.child = child;
	}

	public void setParent (KeyMap parentKeyMap) {
		parent = parentKeyMap;
	}

	public void setChild (KeyMap childKeyMap) {
		child = childKeyMap;
	}

	@Override
	public Shortcut register (String ID, Runnable command, int... keybind) {
		throw new ConsoleRuntimeException("Operation not supported");
	}

	@Override
	public Shortcut getShortcut (int packed) {
		Shortcut s = parent.getShortcut(packed);

		if (child != null && s == null) {
			s = child.getShortcut(packed);
		}
		return s;
	}
}
