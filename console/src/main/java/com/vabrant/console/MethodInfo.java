package com.vabrant.console;

import com.badlogic.gdx.utils.Pool.Poolable;
import com.badlogic.gdx.utils.reflect.Method;

public class MethodInfo implements Poolable {

	private String name;
	private Class declaringClass;
	private Class[] args;
	
	public void set(Method method) {
		this.name = method.getName();
		this.declaringClass = method.getDeclaringClass();
		this.args = method.getParameterTypes();
	}
	
	public void set(MethodInfo info) {
		name = info.getName();
		declaringClass = info.getDeclaringClass();
		args = info.getArgs();
	}
	
	public String getName() {
		return name;
	}
	
	public Class getDeclaringClass() {
		return declaringClass;
	}
	
	public Class[] getArgs() {
		return args;
	}

	public boolean isEqual(MethodInfo info) {
		if(!name.equals(info.getName()) || !ConsoleUtils.equals(args, info.getArgs())) return false;
		return true;
	}
	
	@Override
	public void reset() {
		name = null;
		declaringClass = null;
		args = null;
	}
}
