
package com.vabrant.console.gui.shortcuts;

import com.vabrant.console.gui.KeyboardScope;
import com.vabrant.console.gui.views.View;

public class ParentKeyboardScope extends KeyboardScope {

	protected KeyboardScope childScope;

	public ParentKeyboardScope (View v) {
		this(v.getName() + "");
	}

	public ParentKeyboardScope (String name) {
		super(name);

		class Hello implements KeyMap {

			@Override
			public Shortcut getShortcut (int packed) {
				return null;
			}
		}
	}

	public void setChildScope (KeyboardScope scope) {
		childScope = scope;
	}

	public KeyboardScope getChildScope() {
		return childScope;
	}
}
