package com.vabrant.console;

import java.util.Iterator;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectMap.Entry;

public class CommandObject {

	private int numberOfMethods;
	private Object object;
	private String name;
	private String className;
	final ObjectMap<String, Array<MethodReference>> methods;
	
	public CommandObject(Object object, String name) {
		this.object = object;
		this.name = name;
		className = object.getClass().getSimpleName();
		methods = new ObjectMap<>(5);
	}
	
	void addMethod(MethodReference method) {
		Array<MethodReference> methodNameArray = methods.get(method.getName());
		
		if(methodNameArray == null) {
			methodNameArray = new Array<MethodReference>(1);
			methods.put(method.getName(), methodNameArray);
		}

		methodNameArray.add(method);
		numberOfMethods++;
	}
	
	public boolean containsMethod(String name) {
		return methods.containsKey(name);
	}
	
	public boolean equals(Class[] a1, Class[] a2) {
		if(a1.length != a2.length) return false;
		
		for(int i = 0; i < a1.length; i++) {
			Class c1 = a1[i];
			Class c2 = a2[i];
			
			if(c2 == null) return false;
			
			if(c1 != c2) {
				if(!hasSuperclass(c1, c2)) return false; 
			}
		}
		return true;
	}
	
	private boolean hasSuperclass(Class superclass, Class klass) {
		Class c = klass.getSuperclass();
		while(c != null) {
			if(c.equals(superclass)) {
				return true;
			}
			c = c.getSuperclass();
		}
		return false;
	}
	
	public MethodReference getMethod(String name, Class[] args) {
		MethodReference method = null;
		Array<MethodReference> diffArgMethods = methods.get(name);
		for(int i = 0; i < diffArgMethods.size; i++) {
			MethodReference m = diffArgMethods.get(i);
			if(equals(m.getArgs(), args)) {
				method = m;
				break;
			}
		}
		return method;
	}
	
	public String getClassName() {
		return className;
	}
	
	public String getName() {
		return name;
	}
	
	public Object getObject() {
		return object;
	}
	
	public void printMethods() {
		Iterator<Entry<String, Array<MethodReference>>> iterator = methods.iterator();
		Entry<String, Array<MethodReference>> entry = null;
		while(iterator.hasNext()) {
			entry = iterator.next();
			
			Array<MethodReference> methods = entry.value;
			for(int i = 0; i < methods.size; i++) {
				System.out.println(methods.get(i).toString() + " - object: " + getName());
			}
		}
	}

}
