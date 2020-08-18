package com.vabrant.console;

public abstract class ConsoleReference<T> {
	
	private String name;
	private T reference;
//	private Class<?> referenceClass;
	
	public ConsoleReference(String name, T reference) {
		this.name = name == null ? "" : name;
		this.reference = reference;
	}
	
	public T getReference() {
		return reference;
	}
	
	public Class<?> getReferenceClass() {
		return reference.getClass();
	}
	
	public String getReferenceSimpleName() {
		return reference.getClass().getSimpleName();
	}
	
	public String getName() {
		return name;
	}

}
