
package com.vabrant.console.parsers;

import com.badlogic.gdx.utils.ObjectSet;
import com.badlogic.gdx.utils.Pool;
import com.vabrant.console.ClassReference;
import com.vabrant.console.MethodInfo;

public class MethodArgumentInfo implements Pool.Poolable {

	private String methodName;
	private String referenceName;
	private ObjectSet<MethodInfo> methods;
	private ClassReference classReference;

	public MethodArgumentInfo set (String methodName, ObjectSet<MethodInfo> methods) {
		this.methodName = methodName;
		this.methods = methods;
		return this;
	}

	public void setReferenceName (String referenceName) {
		this.referenceName = referenceName;
	}

	public String getReferenceName () {
		return classReference == null ? null : classReference.getName();
	}

	public void setClassReference (ClassReference classReference) {
		this.classReference = classReference;
	}

	public ClassReference getClassReference () {
		return classReference;
	}

	public void setMethodName (String methodName) {
		this.methodName = methodName;
	}

	public String getMethodName () {
		return methodName;
	}

	public void setMethods (ObjectSet<MethodInfo> methods) {
		this.methods = methods;
	}

	public ObjectSet<MethodInfo> getMethods () {
		return methods;
	}

	public boolean equals (MethodInfo info) {
		if (referenceName != null && !referenceName.equals(info.getClassReference().getName())) return false;
		if (!methodName.equals(info.getMethodReference().getName())) return false;
		return true;
	}

	@Override
	public void reset () {
		methodName = null;
		methods = null;
	}
}
