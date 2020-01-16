package com.vabrant.console;

import com.badlogic.gdx.utils.reflect.Method;
import com.badlogic.gdx.utils.reflect.ReflectionException;

public class CommandMethod {

	private Method method;
	private Class[] args;
	
	public CommandMethod(Method method) {
		this.method = method;
		args = method.getParameterTypes();
	}
	
	public Class getReturnType() {
		return method.getReturnType();
	}
	
	public Class[] getArgs() {
		return args;
	}

	public String getName() {
		return method.getName();
	}
	
	public Method getMethod() {
		return method;
	}
	
	public Object invoke(Object object, Object[] args) throws ReflectionException{
		return method.invoke(object, args);
	}
	
	@Override
	public String toString() {
		StringBuilder buffer = new StringBuilder(30);

		buffer.append(getReturnType());
		buffer.append(' ');

		buffer.append(method.getName());

		if(args.length > 0) {
			buffer.append('(');
			for(int i = 0; i < args.length; i++) {
				buffer.append(args[i].getSimpleName());

				if(args.length > 1 && i != args.length - 1) {
					buffer.append(',');
				}
			}
			buffer.append(')');
		}

		return buffer.toString();
	}
	
}
