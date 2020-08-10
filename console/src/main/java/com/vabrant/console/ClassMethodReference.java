package com.vabrant.console;

import java.util.Iterator;

import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectSet;
import com.badlogic.gdx.utils.reflect.Method;

/**
 * Saves a single instance of a method for a class to be shared by other instances of a class. <br><br>
 * 
 * e.g <br>
 * 
 * <pre><code>
 * public class Guitar {
 * 	
 *     public void pluck() {}
 * }
 * 
 * public class ElectricGuitar extends Guitar {}
 * </code></pre>
 * 
 * All instances of <i> Guitar </i> and <i> Electric Guitar </i> will share the same method reference for the method pluck.
 */
public class ClassMethodReference {
	
	private final ObjectMap<Class<?>, ObjectSet<MethodReference>> references = new ObjectMap<>();
	private final DebugLogger logger = new DebugLogger(ClassMethodReference.class, DebugLogger.DEBUG);
	
	DebugLogger getLogger(){
		return logger;
	}
	
	/**
	 * Returns added methods declared by a class. Methods added from a child but declared in a parent will not be included.
	 * @param c
	 * @return
	 */
	public ObjectSet<MethodReference> getMethods(Class<?> c) {
		return references.get(c);
	}
	
	public boolean hasReferenceMethod(Method method) {
		return getReferenceMethod(method) != null;
	}
	
	public boolean hasReferenceMethod(MethodInfo info) {
		return getReferenceMethod(info.getMethodReference().getMethod()) != null;
	}
	
	public MethodReference getReferenceMethod(Method m) {
		return getReferenceMethod(m.getDeclaringClass(), m.getName(), m.getParameterTypes());
	}
	
	public MethodReference getReferenceMethod(MethodInfo info) {
		return getReferenceMethod(info.getMethodReference().getMethod());
	}
	
	public MethodReference getReferenceMethod(Class<?> declaringClass, String name, Class<?>... argTypes) {
		ObjectSet<MethodReference> classReference = references.get(declaringClass);
		if(classReference == null) return null;
		
		Iterator<MethodReference> it = classReference.iterator();
		while(it.hasNext()) {
			MethodReference ref = it.next();
			if(ref.getName().equals(name) && ConsoleUtils.equals(ref.getArgs(), ConsoleUtils.defaultIfNull(argTypes, ConsoleUtils.EMPTY_ARGUMENT_TYPES))) {
				return ref;
			}
		}
		return null;
	}

	private String argsToString(Class<?>[] args) {
		if(args.length == 0) return "";
		
		StringBuilder builder = new StringBuilder();
		
		for(int i = 0; i < args.length; i++) {
			builder.append(args[i].getSimpleName());
			if(i < (args.length - 1)) builder.append(", ");
		}
		
		return builder.toString();
	}
	
	public MethodReference addReferenceMethod(Method method) {
		ObjectSet<MethodReference> classMethodReferences = references.get(method.getDeclaringClass());
		
		//If there is no reference to this class create one
		if(classMethodReferences == null) {
			classMethodReferences = new ObjectSet<MethodReference>();
			references.put(method.getDeclaringClass(), classMethodReferences);

			StringBuilder builder = new StringBuilder();
			builder.append("Reference:[Class] ");
			builder.append("Name:[");
			builder.append(method.getDeclaringClass().getSimpleName());
			builder.append("] Full:[");
			builder.append(method.getDeclaringClass().getCanonicalName());
			builder.append(']');
			logger.info("[Added]", builder.toString());
		}

		MethodReference reference = getReferenceMethod(method);
		
		if(reference == null) {
			reference = new MethodReference(method);
			classMethodReferences.add(reference);
			
			StringBuilder builder = new StringBuilder()
					.append("Reference:[Method] ")
					.append("Name:[")
					.append(method.getName())
					.append("] Args:[")
					.append(argsToString(method.getParameterTypes()))
					.append("] DeclaringClass:[")
					.append(method.getDeclaringClass().getSimpleName())
					.append("] Full:[")
					.append(method.getDeclaringClass().getCanonicalName())
					.append(']');
			logger.info("[Added]", builder.toString());
		}
		
		return reference;
	}

}
