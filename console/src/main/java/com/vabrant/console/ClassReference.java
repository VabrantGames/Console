package com.vabrant.console;

import com.badlogic.gdx.utils.ObjectSet;

public class ClassReference extends ConsoleReference<Object> {
	
	private ObjectSet<MethodReference> methodReferences;
	
	public ClassReference(String name, Object reference) {
		super(name, reference);
	}
	
	void addMethodReference(MethodReference methodReference) {
		if(methodReferences == null) methodReferences = new ObjectSet<>();
		methodReferences.add(methodReference);
	}
	
	public ObjectSet<MethodReference> getMethodReferences() { 
		return methodReferences;
	}

}
