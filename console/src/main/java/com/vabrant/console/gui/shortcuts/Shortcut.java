
package com.vabrant.console.gui.shortcuts;

import com.vabrant.console.KeyboardScope;

public class Shortcut {

	private int packed;
	private int[] keybind;
	private KeyboardScope scope;
	private Runnable command;
	private String description;

	public Shortcut () {
		keybind = new int[4];
	}

	public void setDescription (String description) {
		this.description = description;
	}

	public String getDescription () {
		return description;
	}

	public void setKeybindPacked (int packed) {
		this.packed = packed;
	}

	public int getKeybindPacked () {
		return packed;
	}

	public void setKeybind (int[] keybind) {
		this.keybind = keybind;
	}

	public int[] getKeybind () {
		return keybind;
	}

	public void setScope (KeyboardScope scope) {
		this.scope = scope;
	}

	public KeyboardScope getScope () {
		return scope;
	}

	public void setConsoleCommand (Runnable command) {
		this.command = command;
	}

	public Runnable getConsoleCommand () {
		return command;
	}
}
