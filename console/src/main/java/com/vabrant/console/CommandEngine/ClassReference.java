
package com.vabrant.console.CommandEngine;

public abstract class ClassReference<T> {

	protected String name;
	protected Class<?> referenceClass;

	protected ClassReference (String name, Class<?> referenceClass) {
		this.name = name == null ? "" : name;
		this.referenceClass = referenceClass;
	}

	public abstract T getReference ();

	public Class<?> getReferenceClass () {
		return referenceClass;
	}

	public String getReferenceSimpleName () {
		return referenceClass.getSimpleName();
	}

	public String getName () {
		return name;
	}

}
