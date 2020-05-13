package com.vabrant.console;

public class InstanceReference extends ClassReference {

	private Object instance;
	
	public InstanceReference(String name, Object instance) {
		super(name, instance.getClass());
		this.instance = instance;
	}
	
	public Object getInstance() {
		return instance;
	}

}