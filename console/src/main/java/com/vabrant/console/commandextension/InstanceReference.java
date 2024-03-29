
package com.vabrant.console.commandextension;

public class InstanceReference extends ClassReference<Object> {

	private Object instance;

	public InstanceReference (String name, Object instance) {
		super(name, instance.getClass());
		this.instance = instance;
	}

	@Override
	public Object getReference () {
		return instance;
	}

}
