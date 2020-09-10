package com.vabrant.console;

import com.badlogic.gdx.utils.reflect.Method;
import com.badlogic.gdx.utils.reflect.ReflectionException;

public class MethodReference {

	private Method method;
	private Class<?>[] args;
	
	public MethodReference(Method method) {
		this.method = method;
		this.method.setAccessible(true);

		//If there is no args use the static EMPTY_ARGS 
		Class<?>[] tmp = method.getParameterTypes();
		args = tmp.length == 0 ? ConsoleUtils.EMPTY_ARGUMENT_TYPES : tmp;
	}
	
	public Class<?> getReturnType() {
		return method.getReturnType();
	}
	
	public Class<?>[] getArgs() {
		return args;
	}
	
	public Class<?> getDeclaringClass() {
		return method.getDeclaringClass();
	}

	public String getName() {
		return method.getName();
	}
	
	public Method getMethod() {
		return method;
	}
	
	public Object invoke(Object object, Object[] args) throws ReflectionException {
		return method.invoke(object, args);
	}
	
	public boolean isEqual(String name, Class<?>[] args) {
		if(getName().equals(name) && ConsoleUtils.equals(this.args, ConsoleUtils.defaultIfNull(args, ConsoleUtils.EMPTY_ARGUMENT_TYPES))) return true;
		return false;
	}
	
	public boolean isEqual(Class<?>[] args) {
		if(ConsoleUtils.equals(this.args, ConsoleUtils.defaultIfNull(args, ConsoleUtils.EMPTY_ARGUMENT_TYPES))) return true;
		return false;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder(30);

		builder.append(getReturnType());
		builder.append(' ');

		builder.append(method.getName());

		if(args.length > 0) {
			builder.append('(');
			for(int i = 0; i < args.length; i++) {
				builder.append(args[i].getSimpleName());

				if(args.length > 1 && i != args.length - 1) {
					builder.append(',');
				}
			}
			builder.append(')');
		}

		return builder.toString();
	}
	
//	@Override
//	public String toString() {
//		StringBuilder b = new StringBuilder();
//		b.append("MethodInfo: ");
//		b.append("Name: " + getName());
//		b.append(" ");
//		
//		b.append('[');
//		Class<?>[] args = getArgs();
//		for(int i = 0; i < args.length; i++) {
//			b.append(args[i].getSimpleName());
//			if(i < (args.length - 1)) b.append(',');
//		}
//		b.append(']');
//		
//		return b.toString();
//	}
	
}
