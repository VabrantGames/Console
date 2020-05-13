package com.vabrant.console;

import java.util.Iterator;

import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectSet;
import com.badlogic.gdx.utils.reflect.Method;

public class ClassMethodReference {
	
	public final static String CLASS_DESCRIPTION = "Reference:[%s] Name:[%s] Full:[%s]";
	public final static String METHOD_DESCRIPTION = "Reference:[%s] Name:[%s] Args:[%s] DeclaringClass:[%s] Full[%s]";
	private final ObjectMap<Class, ObjectSet<MethodReference>> classReferences = new ObjectMap<>();
	final DebugLogger logger = new DebugLogger(ClassMethodReference.class, DebugLogger.DEBUG);
	
	public ObjectSet<MethodReference> getClassMethods(Class c) {
		return classReferences.get(c);
	}
	
	public void addReferenceMethod(Method method) {
		ObjectSet<MethodReference> classMethodsReference = classReferences.get(method.getDeclaringClass());
		
		//If there is no reference for this class create one
		if(classMethodsReference == null) {
			classMethodsReference = new ObjectSet<MethodReference>();
			classReferences.put(method.getDeclaringClass(), classMethodsReference);
			logger.debug(
					ConsoleUtils.ADDED_TAG,
					String.format(
							CLASS_DESCRIPTION, 
							"Class", 
							method.getDeclaringClass().getSimpleName(),
							method.getDeclaringClass().getCanonicalName()));
		}
		
		logger.debug(
				ConsoleUtils.ADDED_TAG,
				String.format(
						METHOD_DESCRIPTION, 
						"Method", 
						method.getName(), 
						argsToString(method.getParameterTypes()), 
						method.getDeclaringClass().getSimpleName(),
						method.getDeclaringClass().getCanonicalName()));
		classMethodsReference.add(new MethodReference(method));
	}
	
	private String argsToString(Class[] args) {
		if(args.length == 0) return "";
		
		StringBuilder builder = new StringBuilder();
		
		for(int i = 0; i < args.length; i++) {
			builder.append(args[i].getSimpleName());
			if(i < (args.length - 1)) builder.append(',');
		}
		
		return builder.toString();
	}
	
	public boolean hasReferenceMethod(Method method) {
		return getReferenceMethod(method) != null;
	}
	
	public boolean hasReferenceMethod(MethodInfo info) {
		return getReferenceMethod(info) != null;
	}
	
	public MethodReference getReferenceMethod(Method m) {
		return getReferenceMethod(m.getDeclaringClass(), m.getName(), m.getParameterTypes());
	}
	
	public MethodReference getReferenceMethod(MethodInfo info) {
		return getReferenceMethod(info.getDeclaringClass(), info.getName(), info.getArgs());
	}
	
	public MethodReference getReferenceMethod(Class c, String name, Class... args) {
		ObjectSet<MethodReference> classReference = classReferences.get(c);
		if(classReference == null) return null;
		
		Iterator<MethodReference> it = classReference.iterator();
		while(it.hasNext()) {
			MethodReference ref = it.next();
			if(ref.getName().equals(name) && ConsoleUtils.equals(ref.getArgs(), ConsoleUtils.defaultIfNull(args, ConsoleUtils.EMPTY_ARGS))) {
				return ref;
			}
		}
		return null;
	}
	
	public void clear() {
		classReferences.clear();
	}

}
