package com.vabrant.console;

import java.util.Iterator;

import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectSet;
import com.badlogic.gdx.utils.reflect.Method;
import com.vabrant.console.ConsoleCache.MethodInfo;

/**
 */
public class ClassMethodReference {
	
	public final static String CLASS_DESCRIPTION = "Reference:[%s] Name:[%s] Full:[%s]";
	public final static String METHOD_DESCRIPTION = "Reference:[%s] Name:[%s] DeclaringClass:[%s] Args:[%s]";
	private static final ObjectMap<Class, ObjectSet<MethodReference>> classReferences = new ObjectMap<>();
	private static final DebugLogger logger = new DebugLogger(ClassMethodReference.class, DebugLogger.DEBUG);
	
	public static ObjectSet<MethodReference> getClassMethods(Class c) {
		return classReferences.get(c);
	}
	
	public static void addReferenceMethod(Method method) {
		ObjectSet<MethodReference> classMethodsReference = classReferences.get(method.getDeclaringClass());
		
		//If there is no reference for this class create one
		if(classMethodsReference == null) {
			classMethodsReference = new ObjectSet<MethodReference>();
			classReferences.put(method.getDeclaringClass(), classMethodsReference);
			logger.debug(String.format(CLASS_DESCRIPTION, "Class", method.getDeclaringClass().getSimpleName(), method.getDeclaringClass().getCanonicalName()));
		}
		
		logger.debug(String.format(METHOD_DESCRIPTION, "Method", method.getName(), method.getDeclaringClass().getCanonicalName(), argsToString(method.getParameterTypes())));
		classMethodsReference.add(new MethodReference(method));
	}
	
	private static String argsToString(Class[] args) {
		if(args.length == 0) return "0";
		
		StringBuilder builder = new StringBuilder();
		
		for(int i = 0; i < args.length; i++) {
			builder.append(args[i].getSimpleName());
			if(i < (args.length - 1)) builder.append(',');
		}
		
		return builder.toString();
	}
	
	public static boolean hasReferenceMethod(Method method) {
		return getReferenceMethod(method) != null;
	}
	
	public static boolean hasReferenceMethod(MethodInfo info) {
		return getReferenceMethod(info) != null;
	}
	
	public static MethodReference getReferenceMethod(Method m) {
		return getReferenceMethod(m.getDeclaringClass(), m.getName(), m.getParameterTypes());
	}
	
	public static MethodReference getReferenceMethod(MethodInfo info) {
		return getReferenceMethod(info.getDeclaringClass(), info.getName(), info.getArgs());
	}
	
	public static MethodReference getReferenceMethod(Class c, String name, Class... args) {
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


}
