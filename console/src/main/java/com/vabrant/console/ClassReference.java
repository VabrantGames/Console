package com.vabrant.console;

public abstract class ClassReference {
	
	private String name;
	private Class referenceClass;
	
	public ClassReference(String name, Class referenceClass) {
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
