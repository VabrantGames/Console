package com.vabrant.console.commandsections;

import com.badlogic.gdx.utils.Array;

public class ArgumentGroupInfo {
	
	private CommandSection owningExecutable;
	private Array<Array<CommandSection>> arguments;
	private Array<CommandSection> currentArgument;
	
	public ArgumentGroupInfo(CommandSection owningExecutable) {
		this.owningExecutable = owningExecutable;
		arguments = new Array<>();
	}
	
	public CommandSection getOwningExecutable() {
		return owningExecutable;
	}
	
	public void addSectionForArgument(CommandSection section) {
		if(currentArgument == null) nextArgument();
		currentArgument.add(section);
	}
	
	public void nextArgument() {
		currentArgument = new Array<>();
		arguments.add(currentArgument);
	}
	
	public Array<Array<CommandSection>> getArguments() {
		return arguments;
	}

}
