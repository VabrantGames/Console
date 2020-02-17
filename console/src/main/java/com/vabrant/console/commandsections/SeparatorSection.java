package com.vabrant.console.commandsections;

import com.vabrant.console.TextBox;

public class SeparatorSection extends CommandSection {

	private char separator = TextBox.NULL_CHARACTER;
	
	public void set(char separator) {
		this.separator = separator;
	}
	
	public char get() {
		return separator;
	}
	
	@Override
	public void reset() {
		super.reset();
		separator = TextBox.NULL_CHARACTER;
	}
}
