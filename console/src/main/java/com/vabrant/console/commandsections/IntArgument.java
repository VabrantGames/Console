package com.vabrant.console.commandsections;

import com.vabrant.console.Console;

public class IntArgument implements Argument {

	private int value = 0;
	
	@Override
	public void set(Console console, String s) throws Exception{
		value = Integer.parseInt(s);
	}
	
	public int get() {
		return value;
	}
	
	@Override
	public Class getArgumentType() {
		return int.class;
	}

	@Override
	public Object getArgument() {
		return value;
	}
	
	@Override
	public void reset() {
		value = 0;
	}

}
