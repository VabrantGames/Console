package com.vabrant.console.commandsections;

import com.vabrant.console.Console;

public class FloatArgument implements Argument {

	private float value = 0;
	
	@Override
	public void set(Console console, String s) throws Exception{
		value = Float.parseFloat(s);
	}
	
	public float get() {
		return value;
	}
	
	@Override
	public Class getArgumentType() {
		return float.class;
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
