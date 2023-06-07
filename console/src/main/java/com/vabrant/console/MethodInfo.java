
package com.vabrant.console;

import com.badlogic.gdx.utils.reflect.ReflectionException;

public class MethodInfo {

	private final ClassReference<?> classReference;
	private final MethodReference methodReference;

	public MethodInfo (ClassReference<?> classReference, MethodReference methodReference) {
		this.classReference = classReference;
		this.methodReference = methodReference;
	}

	public ClassReference getClassReference () {
		return classReference;
	}

	public MethodReference getMethodReference () {
		return methodReference;
	}

	public Object invoke (Object[] args) throws ReflectionException {
		return methodReference.invoke(classReference.getReference(), args);
	}

}
