
package com.vabrant.console.gui.shortcuts;

import com.vabrant.console.KeyboardScope;

/**
 *
 */
public class ParentKeyboardScope implements KeyboardScope {

	protected KeyboardScope parentScope;
	protected KeyboardScope childScope;

	public ParentKeyboardScope () {
	}

	public ParentKeyboardScope (KeyboardScope parentScope) {
		setParent(parentScope);
	}

	public void setParent (KeyboardScope parentScope) {
		this.parentScope = parentScope;
	}

	public void setChild (KeyboardScope scope) {
		childScope = scope;
	}

	public KeyboardScope getParentScope () {
		return parentScope;
	}

	public KeyboardScope getChildScope () {
		return childScope;
	}

	@Override
	public boolean equals (Object o) {
		return parentScope.equals(o) || childScope != null && childScope.equals(o);
	}
}
