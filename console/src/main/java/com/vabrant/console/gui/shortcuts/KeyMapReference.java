
package com.vabrant.console.gui.shortcuts;

@Deprecated
public class KeyMapReference<T extends DefaultKeyMap> implements KeyMap {

	private T reference;

	public KeyMapReference () {
		this(null);
	}

	public KeyMapReference (T keyMap) {
		reference = keyMap;
	}

	public void setReference (T reference) {
		this.reference = reference;
	}

	public T getReference () {
		return reference;
	}

	@Override
	public Shortcut getShortcut (int keybindPacked) {
		return reference == null ? null : reference.getShortcut(keybindPacked);
	}
}
