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
	final ObjectMap<String, Array<CommandMethod>> methods;
	
	public CommandObject(Object object, String name) {
		this.object = object;
		this.name = name;
		className = object.getClass().getSimpleName();
		methods = new ObjectMap<>(5);
	}
	
	void addMethod(CommandMethod method) {
		Array<CommandMethod> methodNameArray = methods.get(method.getName());
		
		if(methodNameArray == null) {
			methodNameArray = new Array<CommandMethod>(1);
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
	
	public CommandMethod getMethod(String name, Class[] args) {
		CommandMethod method = null;
		Array<CommandMethod> diffArgMethods = methods.get(name);
		for(int i = 0; i < diffArgMethods.size; i++) {
			CommandMethod m = diffArgMethods.get(i);
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
		Iterator<Entry<String, Array<CommandMethod>>> iterator = methods.iterator();
		Entry<String, Array<CommandMethod>> entry = null;
		System.out.println("CommandObject: " + name);
		System.out.println("\tMethods:");
		while(iterator.hasNext()) {
			entry = iterator.next();
			
			Array<CommandMethod> methods = entry.value;
			for(int i = 0; i < methods.size; i++) {
				System.out.println("\t\t" + methods.get(i).toString());
			}
		}
	}

}
