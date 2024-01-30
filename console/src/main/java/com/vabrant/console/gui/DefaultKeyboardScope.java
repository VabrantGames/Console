
package com.vabrant.console.gui;

import com.vabrant.console.KeyboardScope;
import com.vabrant.console.gui.views.View;

import java.util.Objects;

public class DefaultKeyboardScope implements KeyboardScope {

	private final String name;

	public DefaultKeyboardScope (View v) {
		name = v.getName() + "-scope";
	}

	public DefaultKeyboardScope (String name) {
		this.name = name;
	}

	public String getName () {
		return name;
	}

	@Override
	public boolean equals (Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		KeyboardScope that = (KeyboardScope)o;
// return name.equals(that.name);
		return hashCode() == that.hashCode();
	}

	@Override
	public int hashCode () {
		return Objects.hash(name);
	}
}
