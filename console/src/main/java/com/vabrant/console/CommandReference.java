package com.vabrant.console;

public abstract class CommandReference {
	
	private String name;
	private Class referenceClass;
	
	public CommandReference(String name, Class referenceClass) {
		this.name = name == null ? "" : name;
		this.referenceClass = referenceClass;
	}
	
	public Class getReferenceClass() {
		return referenceClass;
	}
	
	public String getReferenceSimpleName() {
		return referenceClass.getSimpleName();
	}
	
	public String getName() {
		return name;
	}

}
