package com.vabrant.console.gui.shortcuts;

public class ParentKeyMap implements KeyMap {

	private final KeyMap parent;
	private KeyMap child;

	public ParentKeyMap (KeyMap parent) {
		this(parent, null);
	}

	public ParentKeyMap (KeyMap parent, KeyMap child) {
		this.parent = parent;
		this.child = child;
	}

	public void setChild (KeyMap childKeyMap) {
		child = childKeyMap;
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
