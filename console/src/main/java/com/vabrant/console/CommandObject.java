package com.vabrant.console;

public class CommandObject {

	private int numberOfMethods;
	private Object object;
	private String name;
	private String className;
	
	public CommandObject(Object object, String name) {
		this.object = object;
		this.name = name;
		className = object.getClass().getSimpleName();
	}
	
	public String getClassName() {
		return className;
	}
	
	public String getName() {
		return name;
	}
	
	public Object get() {
		return object;
	}

}
