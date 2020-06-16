package com.vabrant.console.commandsections;

public abstract class BasicArgument<T> implements Argument<T> {
	
	protected CommandSection section;

	public void setSection(CommandSection section) {
		this.section = section;
	}

	@Override
	public void reset() {
		section = null;
	}
	
	protected abstract void setArgument(T argument);
	protected abstract void parse(String sectionString) throws RuntimeException;

}
