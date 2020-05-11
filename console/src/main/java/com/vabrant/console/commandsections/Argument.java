package com.vabrant.console.commandsections;

import com.badlogic.gdx.utils.Pool.Poolable;
import com.vabrant.console.Console;

public abstract class Argument<T> implements Poolable{
	
	protected CommandSection section;
	
	public abstract void setArgument(T argument);
	public abstract Class<T> getArgumentType();
	public abstract T getArgument();
	
	public void set(CommandSection section) {
		this.section = section;
	}
	
	@Override
	public void reset() {
		section = null;
	}
}
