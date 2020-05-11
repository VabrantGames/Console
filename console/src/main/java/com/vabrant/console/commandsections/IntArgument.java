package com.vabrant.console.commandsections;

import com.vabrant.console.Console;

public class IntArgument extends Argument<Integer> {

	private int value = 0;
	
	@Override
	public void set(Console console, String s) throws Exception{
		value = Integer.parseInt(s);
	}
	
	@Override
	public void setArgument(Integer argument) {
		value = argument;
	}
	
	public int get() {
		return value;
	}
	
	@Override
	public Class<Integer> getArgumentType() {
		return int.class;
	}

	@Override
	public Integer getArgument() {
		return value;
	}
	
	@Override
	public void reset() {
		value = 0;
	}

}
