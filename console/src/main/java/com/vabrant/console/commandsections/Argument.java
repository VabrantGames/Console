package com.vabrant.console.commandsections;

import com.badlogic.gdx.utils.Pool.Poolable;
import com.vabrant.console.Console;

public interface Argument extends Poolable{
	public Class getArgumentType();
	public Object getArgument();
	public void set(Console console, String section) throws Exception;
}
