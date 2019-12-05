package com.vabrant.console;

import com.badlogic.gdx.utils.reflect.Method;

public class CommandMethod {

	private boolean force;
	private Method method;
	
	public CommandMethod(Method method, boolean force) {
		this.method = method;
		this.force = force;
	}

	public String getName() {
		return method.getName();
	}
}
