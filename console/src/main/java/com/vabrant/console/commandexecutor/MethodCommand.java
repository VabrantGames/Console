
package com.vabrant.console.commandexecutor;

import com.badlogic.gdx.utils.reflect.Method;

import java.util.Arrays;
import java.util.Objects;

public class MethodCommand implements Command {

	private String successMessage;
	private final ClassReference<?> classReference;

	private final Method method;
	private Class[] args;

	public MethodCommand (ClassReference<?> classReference, Method method) {
		this.classReference = classReference;
		this.method = method;
		this.method.setAccessible(true);
		args = method.getParameterTypes();
	}

	@Override
	public void setSuccessMessage (String successMessage) {
		this.successMessage = successMessage;
	}

	@Override
	public String getSuccessMessage () {
		return successMessage;
	}

	public ClassReference getClassReference () {
		return classReference;
	}

	public Method getMethod () {
		return method;
	}

	public Class getReturnType () {
		return method.getReturnType();
	}

	public Class[] getArgs () {
		return args;
	}

	public Class getDeclaringClass () {
		return method.getDeclaringClass();
	}

	public String getMethodName () {
		return method.getName();
	}

	@Override
	public Object execute (Object[] o) throws Exception {
		return method.invoke(classReference.getReference(), o);
	}

	@Override
	public String toString () {
		StringBuilder buffer = new StringBuilder(30);

		buffer.append(getReturnType());
		buffer.append(' ');

		buffer.append(getMethodName());

		buffer.append('(');
		for (int i = 0; i < args.length; i++) {
			buffer.append(args[i].getSimpleName());

			if (args.length > 1 && i != args.length - 1) {
				buffer.append(',');
			}
		}
		buffer.append(')');

		return buffer.toString();
	}

	@Override
	public boolean equals (Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		MethodCommand that = (MethodCommand)o;
		return classReference.equals(that.classReference) && method.equals(that.method) && Arrays.equals(args, that.args);
	}

	@Override
	public int hashCode () {
		int result = Objects.hash(classReference, method);
		result = 31 * result + Arrays.hashCode(args);
		return result;
	}
}
