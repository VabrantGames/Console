
package com.vabrant.console.parsers;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.vabrant.console.ConsoleUtils;
import com.vabrant.console.Executable;
import com.vabrant.console.MethodInfo;

public class MethodContainer implements Executable<Object, Object>, Poolable {

	private Array<Object> arguments;
	private MethodInfo methodInfo;
	private Object returnValue;

	public MethodContainer () {
		arguments = new Array<>(6);
	}

	public MethodInfo getMethodInfo () {
		return methodInfo;
	}

	public void addArgument (Object o) {
		arguments.add(o);
	}

	public Array<Object> getArguments () {
		return arguments;
	}

	public void setMethodInfo (MethodInfo methodInfo) {
		this.methodInfo = methodInfo;
	}

	public Class<?> getReturnType () {
		return methodInfo.getMethodReference().getReturnType();
	}

	public Object getReturnValue () {
		return returnValue;
	}

	private Object[] convertArguments () {
		if (arguments.size == 0) return ConsoleUtils.EMPTY_ARGUMENTS;

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
		returnValue = methodInfo.invoke(convertArguments());
		return null;
	}

	@Override
	public void reset () {
		arguments.clear();
		methodInfo = null;
		returnValue = null;
	}
}
