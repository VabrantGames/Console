package com.vabrant.console.commandsections;

import com.badlogic.gdx.utils.Pools;
import com.vabrant.console.Console;

public class ArgumentSection extends CommandSection {
	
	private String sectionString;
	private Argument argument;
	
	public void setArgument(Console console, Argument argument, String section) throws Exception{
		this.sectionString = section;
		
		if(argument == null) return;
		
		this.argument = argument;
		this.argument.set(console, section);
		setValid(true);
	}
	
	public void removeArgument() {
		if(argument == null) return;
		Pools.free(argument);
		argument = null;
	}
	
	public Argument getArgument() {
		return argument;
	}
	
	public String getSectionString() {
		return sectionString;
	}
	
	@Override
	public boolean isValid() {
		return argument == null ? false : super.isValid();
	}
	
	@Override
	public void reset() {
		super.reset();
		removeArgument();
	}

}
