package com.vabrant.console.commandsections;

import com.vabrant.console.Console;

public class DoubleArgument implements Argument {

	private double value = 0;
	
	public void set(Console console, String section) throws Exception{
		value = Double.parseDouble(section);
	}
	
	public double get() {
		return value;
	}
	
	@Override
	public Class getArgumentType() {
		return double.class;
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
