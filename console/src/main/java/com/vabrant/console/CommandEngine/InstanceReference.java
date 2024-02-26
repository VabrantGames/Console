
package com.vabrant.console.CommandEngine;

import java.util.Objects;

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

	@Override
	public boolean equals (Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		InstanceReference that = (InstanceReference)o;
		return instance.equals(that.instance);
	}

	@Override
	public int hashCode () {
		return Objects.hash(instance);
	}
}
