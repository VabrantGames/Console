
package com.vabrant.console.commandexecutor;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.vabrant.console.Executable;
import com.vabrant.console.Utils;

public class MethodContainer implements Executable<Object, Object>, Poolable {

	private Array<Object> arguments;
	private Command command;
	private String commandName;
	private Object returnValue;

	public MethodContainer () {
		arguments = new Array<>(6);
	}

	public void setCommandName (String commandName) {
		this.commandName = commandName;
	}

	public String getCommandName () {
		return commandName;
	}

	public void addArgument (Object o) {
		arguments.add(o);
	}

	public Array<Object> getArguments () {
		return arguments;
	}

	public void setCommand (Command command) {
		this.command = command;
	}

	public Command getCommand () {
		return command;
	}

	public Object getReturnValue () {
		return returnValue;
	}

	private Object[] convertArguments () {
		if (arguments.size == 0) return Utils.EMPTY_ARGUMENTS;

		Object[] args = new Object[arguments.size];
		for (int i = 0, len = arguments.size, idx = len - 1; i < len; i++, idx--) {
			Object arg = arguments.get(idx);

			if (arg instanceof MethodContainer) {
				args[i] = ((MethodContainer)arg).getReturnValue();
			} else {
				args[i] = arguments.get(idx);
			}
		}
		return args;
	}

	@Override
	public Object execute (Object o) throws Exception {
		returnValue = command.execute(convertArguments());
		return null;
	}

	@Override
	public void reset () {
		arguments.clear();
		returnValue = null;
		command = null;
		commandName = null;
	}
}
