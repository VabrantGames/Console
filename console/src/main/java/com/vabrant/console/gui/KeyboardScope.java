
package com.vabrant.console.gui;

import java.util.Objects;

public class KeyboardScope {

	private final String name;

	public KeyboardScope (String name) {
		this.name = name.toLowerCase();
	}

	public String getName () {
		return name;
	}

	@Override
	public boolean equals (Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		KeyboardScope that = (KeyboardScope)o;
		return name.equals(that.name);
	}

	@Override
	public int hashCode () {
		return Objects.hash(name);
	}
}
