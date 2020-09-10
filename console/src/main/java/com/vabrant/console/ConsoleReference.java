package com.vabrant.console;

public abstract class ConsoleReference<T> {
	
	private String name;
	private String simpleName;
	private T reference;
	
	public ConsoleReference(String name, T reference) {
		this.name = name == null ? "" : name;
		this.reference = reference;
		simpleName = reference.getClass().getSimpleName();
	}
	
	public T getReference() {
		return reference;
	}
	
	public Class<?> getReferenceClass() {
		return reference.getClass();
	}
	
	public String getReferenceClassSimpleName() {
		return simpleName;
	}
	
	public String getName() {
		return name;
	}

}
