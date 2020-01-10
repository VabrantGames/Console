package com.vabrant.console;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.badlogic.gdx.utils.reflect.ReflectionException;

public class CommandObject {

	private int numberOfMethods;
	private Object object;
	private String name;
	private String className;
	final HashMap<String, ArrayList<CommandMethod>> methods;
	
	public CommandObject(Object object, String name) {
		this.object = object;
		this.name = name;
		className = object.getClass().getSimpleName();
		methods = new HashMap<>(5);
	}
	
	void addMethod(CommandMethod method) {
		ArrayList<CommandMethod> methodNameArray = methods.get(method.getName());
		
		if(methodNameArray == null) {
			methodNameArray = new ArrayList<CommandMethod>(1);
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
		ArrayList<CommandMethod> diffArgMethods = methods.get(name);
		for(int i = 0; i < diffArgMethods.size(); i++) {
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
		Iterator<Map.Entry<String, ArrayList<CommandMethod>>> iterator = methods.entrySet().iterator();
		Map.Entry<String, ArrayList<CommandMethod>> entry = null;
		System.out.println("CommandObject: " + name);
		System.out.println("\tMethods:");
		while(iterator.hasNext()) {
			entry = iterator.next();
			
			ArrayList<CommandMethod> methods = entry.getValue();
			for(int i = 0; i < methods.size(); i++) {
				System.out.println("\t\t" + methods.get(i).toString());
			}
		}
	}

}
