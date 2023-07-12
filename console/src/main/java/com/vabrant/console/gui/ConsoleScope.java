
package com.vabrant.console.gui;

public abstract class ConsoleScope {

	private final String name;

	protected ConsoleScope (String name) {
		this.name = name;
	}

	public String getName () {
		return name;
	}

	public abstract boolean isActive ();
}
