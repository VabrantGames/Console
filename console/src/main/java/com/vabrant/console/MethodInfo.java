package com.vabrant.console;

import com.badlogic.gdx.utils.reflect.ReflectionException;

public class MethodInfo {

	private final ConsoleReference<?> classReference;
	private final MethodReference methodReference;
	
	public MethodInfo(ConsoleReference<?> classReference, MethodReference methodReference) {
		this.classReference = classReference;
		this.methodReference = methodReference;
	}

	public String getName() {
		return methodReference.getName();
	}
	
	public Class<?> getDeclaringClass() {
		return methodReference.getDeclaringClass();
	}
	
	public Class<?>[] getArgs() {
		return methodReference.getArgs();
	}
	
	public Class<?> getReturnType() {
		return methodReference.getReturnType();
	}
	
	public ConsoleReference<?> getClassReference() {
		return classReference;
	}
	
	public MethodReference getMethodReference() {
		return methodReference;
	}

	public boolean isEqual(MethodInfo info) {
		if(!getName().equals(info.getName()) || !ConsoleUtils.equals(getArgs(), info.getArgs())) return false;
		return true;
	}
	
	public Object invoke(Object[] args) throws ReflectionException {
		return methodReference.invoke(classReference.getReference(), args);
	}
	
	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		b.append("MethodInfo: ");
		b.append("Name: " + getName());
		b.append(" ");
		
		b.append('[');
		Class<?>[] args = getArgs();
		for(int i = 0; i < args.length; i++) {
			b.append(args[i].getSimpleName());
			if(i < (args.length - 1)) b.append(',');
		}
		b.append(']');
		
		return b.toString();
	}
	
}
