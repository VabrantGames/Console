package com.vabrant.console.commandsections;

import com.vabrant.console.Console;

public class LongArgument implements Argument {

	private long value = 0;
	
	@Override
	public void set(Console console, String s) throws Exception{
		value = Long.parseLong(s.substring(0, s.length() - 1));
	}
	
	@Override
	public Class getArgumentType() {
		return long.class;
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
