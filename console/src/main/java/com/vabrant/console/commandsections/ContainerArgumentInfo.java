package com.vabrant.console.commandsections;

import com.badlogic.gdx.utils.Array;

public class ContainerArgumentInfo {
	
	private CommandSection leadingSection;
	private Array<Array<CommandSection>> arguments;
	private Array<CommandSection> currentArgument;
	
	public ContainerArgumentInfo(CommandSection leadingSection) {
		this.leadingSection = leadingSection;
		arguments = new Array<>();
	}
	
	public CommandSection getLeadingSection() {
		return leadingSection;
	}
	
	public void addArgumentFragment(CommandSection section) {
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
