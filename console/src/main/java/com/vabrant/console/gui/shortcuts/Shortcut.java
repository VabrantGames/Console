
package com.vabrant.console.gui.shortcuts;

import com.vabrant.console.gui.ConsoleScope;

public class Shortcut {

	private int packed;
	private int[] keybind;
	private ConsoleScope scope;
	private ShortcutCommand command;
	private final String name;
	private String description;

	public Shortcut () {
		this(null);
	}

	public Shortcut (String name) {
		this.name = name;
		keybind = new int[4];
	}

	public String getName () {
		return name;
	}

	public void setKeybindPacked (int packed) {
		this.packed = packed;
	}

	public int getKeybindPacked () {
		return packed;
	}

	public void setKeybind (int[] keybind) {
		if (keybind.length > 4) {
			throw new RuntimeException("Keybind ");
		}
		this.keybind = keybind;
	}

	public int[] getKeybind () {
		return keybind;
	}

	public void setScope (ConsoleScope scope) {
		this.scope = scope;
	}

	public ConsoleScope getScope () {
		return scope;
	}

	public void setConsoleCommand (ShortcutCommand command) {
		this.command = command;
	}

	public ShortcutCommand getConsoleCommand () {
		return command;
	}
}
