package com.vabrant.console.commandsections;

import com.vabrant.console.CommandObject;
import com.vabrant.console.Console;

public class ObjectArgument extends Argument<CommandObject> {

	private CommandObject commandObject;
	
//	@Override
//	public void set(Console console, String section) throws Exception {
//		commandObject = console.getCommandObject(section);
//		if(commandObject == null) throw new IllegalArgumentException("Object " + section + " doesn't exists");
//	}
	
//	public void set(CommandObject commandObject) {
//		this.commandObject = commandObject;
//	}
	
	@Override
	public void setArgument(CommandObject argument) {
		
	}
	
	public CommandObject get() {
		return commandObject;
	}
	
	@Override
	public Class getArgumentType() {
		return commandObject == null ? null : commandObject.getObject().getClass();
	}

	@Override
	public CommandObject getArgument() {
//		return commandObject.getObject();
	}
	
	@Override
	public void reset() {
		commandObject = null;
	}

}
