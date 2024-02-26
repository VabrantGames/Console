
package com.vabrant.console.CommandEngine;

import java.util.Objects;

public class StaticReference extends ClassReference<Class<?>> {

	public StaticReference (String name, Class<?> c) {
		super(name, c);
	}

	@Override
	public Class<?> getReference () {
		return getReferenceClass();
	}

	@Override
	public boolean equals (Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		StaticReference that = (StaticReference) o;
		return getReference().equals(that.getReference());
	}

	@Override
	public int hashCode () {
		return Objects.hash(getReference());
	}
}
