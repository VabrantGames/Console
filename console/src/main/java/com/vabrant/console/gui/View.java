
package com.vabrant.console.gui;

public abstract class View {

	private boolean isHidden = true;
	protected final String name;
	protected GUIConsole console;

	protected View (String name) {
		this.name = name;
	}

	public String getName () {
		return name;
	}

	public void setConsole (GUIConsole console) {
		this.console = console;
	}

	public void setHidden (boolean hidden) {
		isHidden = hidden;
	}

	public boolean isHidden () {
		return isHidden;
	}

	public abstract boolean hasFocus ();

	public abstract void focus ();

	public abstract int getZIndex ();

	public abstract void resize (float oldWidth, float oldHeight, float width, float height);

	public abstract boolean hit (float x, float y);
}
