package com.vabrant.console;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectMap.Values;
import com.badlogic.gdx.utils.ObjectSet;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.Field;
import com.badlogic.gdx.utils.reflect.Method;
import com.badlogic.gdx.utils.reflect.ReflectionException;
import com.vabrant.console.annotation.ConsoleMethod;
import com.vabrant.console.annotation.ConsoleObject;

public class ConsoleCache {
	
	public final static ConsoleCache GLOBAL_CACHE = null;
	public final static String CLASS_REFERENCE_DESCRIPTION = "Reference:[%S] Name:[%s] Class:[%s] Full:[%s]";
	 
	private final ObjectMap<String, ClassReference> classReferences = new ObjectMap<>(20);
	
	//Holds an array of references that share a method name
	private final ObjectMap<String, ObjectSet<ClassReference>> referencesWithSpecifiedNamedMethod = new ObjectMap<>();
	
	//All methods belonging to a reference
	private final ObjectMap<ClassReference, ObjectSet<MethodInfo>> methodsByReference = new ObjectMap<>();
	
	//Methods grouped by name
	private final ObjectMap<String, ObjectSet<MethodInfo>> methodsByName = new ObjectMap<>();
	private final ClassMethodReference classMethodReference = new ClassMethodReference();
	
	private final DebugLogger logger = new DebugLogger(ConsoleCache.class, DebugLogger.NONE);
	
	public void setLogLevel(int level) {
		logger.setLevel(level);
		classMethodReference.getLogger().setLevel(level);
	}
	
	/**
	 * Returns a class reference for the specified name. Null is returned if no reference is found.
	 * @param name
	 * @return
	 */
	public ClassReference getReference(String name) {
		return classReferences.get(name);
	}
	
	/**
	 * Returns a class reference for the specified object. Null is returned if no reference is found.
	 * @param name
	 * @return
	 */
	public ClassReference getReference(Object object) {
		if(object == null) return null;

		Values<ClassReference> values = classReferences.values();
		for(ClassReference r : values) {
			if(r.getReference().equals(object)) return r;
		}
		return null;
	}
	
	/**
	 * Checks if a reference exists for the specified name.
	 * @param name
	 * @return
	 */
	public boolean hasReference(String name) {
		return classReferences.containsKey(name);
	}
	
	public boolean hasReference(Object object) {
		return getReference(object) != null;
	}

	/**
	 * Check if a method exists for the specified name.
	 * @param name
	 * @return
	 */
	public boolean hasMethod(String name) {
		return referencesWithSpecifiedNamedMethod.containsKey(name);
	}
	
	/**
	 * Check if a reference has a method added.
	 * @param referenceName
	 * @param methodName
	 * @param args
	 * @return
	 */
	public boolean hasMethod(String referenceName, String methodName, Class<?>... args) {
		ClassReference reference = getReference(referenceName);
		if(reference == null) return false;
		return hasMethod(reference, methodName, args);
	}
	
	public boolean hasMethod(ClassReference classReference, String methodName, Class<?>... args) {
		if(!hasMethod(methodName)) return false;
		
		ObjectSet<MethodInfo> methods = methodsByReference.get(classReference);
		if(methods == null) return false;
		
		for(MethodInfo info : methods) {
			if(info.getName().equals(methodName) && ConsoleUtils.equals(info.getArgs(), ConsoleUtils.defaultIfNull(args, ConsoleUtils.EMPTY_ARGUMENT_TYPES))) return true;
		}
		return false;
	}
	
	public ObjectSet<MethodInfo> getAllMethodsWithName(String name){
		ObjectSet<MethodInfo> methods = methodsByName.get(name);
		if(methods == null) throw new RuntimeException("Method [" + name + "] not found.");
		return methods;
	}
	
	public ObjectSet<MethodInfo> getAllMethodsByReference(String referenceName){
		ClassReference reference = getReference(referenceName);
		if(reference == null) throw new RuntimeException("Reference " + referenceName + " not found.");
		
		ObjectSet<MethodInfo> methods = methodsByReference.get(reference);
		if(methods == null) throw new RuntimeException("Reference " + referenceName + " has 0 methods added.");
		return methods;
	}

	/**
	 * Adds an object to the cache as a reference. The name of the class is used as the identifier. This same object can have methods added to it later. 
	 * @param object Object used as a reference.
	 * @param referenceID Name used to call the reference.
	 */
	public void addReference(Object object) {
		addReference(object, null);
	}
	
	/**
	 * Adds an object to the cache as a reference. This same object can have methods added to it later. 
	 * @param object Object used as a reference.
	 * @param referenceID Name used to call the reference.
	 */
	public void addReference(Object object, String referenceID) {
		if(object == null) throw new IllegalArgumentException("Object is null");

		ClassReference reference = getReference(object);
		
		//Check if an instance reference is using the object as a reference
		if(reference != null) {
			StringBuilder builder = new StringBuilder(50);
			builder.append("ReferenceType:[Class] ");
			builder.append("Name:[");
			builder.append(reference.getName());
			builder.append("] ");
			builder.append("Class:[");
			builder.append(reference.getReferenceClass().getCanonicalName());
			builder.append(']');
			logger.info("[Conflict] (Reference with object already exists)", builder.toString());
			return;
		}
		
		if(referenceID == null || referenceID.isEmpty()) {
			if(object.getClass().isAnnotationPresent(ConsoleObject.class)) {
				ConsoleObject o = ClassReflection.getAnnotation(object.getClass(), ConsoleObject.class).getAnnotation(ConsoleObject.class);
				referenceID = !o.value().isEmpty() ? o.value() : object.getClass().getSimpleName();
			}
			else {
				referenceID = object.getClass().getSimpleName();
			}
		}

		//Check if an class reference is using the given name
		if(classReferences.containsKey(referenceID)) {
			ClassReference ref = getReference(referenceID);

			StringBuilder builder = new StringBuilder(50);
			builder.append("ReferenceType:[Class] ");
			builder.append("Name:[");
			builder.append(ref.getName());
			builder.append("] Class:[");
			builder.append(ref.getClass().getCanonicalName());
			builder.append(']');
			logger.info("[Conflict] (Reference with name already exists)", builder.toString());
			return;
		}

		reference = new ClassReference(referenceID, object);

		StringBuilder builder = new StringBuilder(50);
		builder.append("ReferenceType:[Class] ");
		builder.append("Name:[");
		builder.append(reference.getName());
		builder.append("] Class:[");
		builder.append(reference.getReferenceClass().getCanonicalName());
		builder.append(']');
		
		logger.info("[Added]", builder.toString());
		
		classReferences.put(referenceID, reference);
	}

	//Adds a method of an object
	public void addMethod(Object object, String methodName, Class<?>... args) {
		if(object == null) throw new IllegalArgumentException("Object is null.");
		if(methodName == null || methodName.isEmpty()) throw new IllegalArgumentException("Invalid method name");

		//Check if a reference with the object exists and contains the method
		ClassReference reference = getReference(object);
		if(reference != null) {
			if(hasMethod(reference, methodName, args)) return;
		}
		
		boolean isStatic = object instanceof Class;
		
		Method method = null;
		try {
			method = ClassReflection.getMethod(isStatic ? (Class<?>)object : object.getClass(), methodName, args);
		}
		catch(ReflectionException e) {
			logger.error(e.getMessage());
			return;
		}

		//Create a reference for the object is one doesn't exists
		if(reference == null) {
			addReference(object, isStatic ? ((Class<?>)object).getSimpleName() : object.getClass().getSimpleName());
			reference = getReference(object);
		}
		
		addMethodToCache(reference, method);
	}
	
	private void addMethodToCache(ClassReference classReference, Method method) {
		MethodReference methodReference = classMethodReference.getReferenceMethod(method);
		if(methodReference == null) {
			methodReference = classMethodReference.addReferenceMethod(method);
		}
		
		MethodInfo info = new MethodInfo(classReference, methodReference);
		
		//Get a set containing all added methods for the reference
		ObjectSet<MethodInfo> allMethodsForReference = methodsByReference.get(classReference);
		if(allMethodsForReference == null) {
			allMethodsForReference = new ObjectSet<>();
			methodsByReference.put(classReference, allMethodsForReference);
		}
		allMethodsForReference.add(info);

		ObjectSet<ClassReference> referencesWithSpecifiedNamedMethod = this.referencesWithSpecifiedNamedMethod.get(method.getName());
		if(referencesWithSpecifiedNamedMethod == null) {
			referencesWithSpecifiedNamedMethod = new ObjectSet<ClassReference>();
			this.referencesWithSpecifiedNamedMethod.put(method.getName(), referencesWithSpecifiedNamedMethod);
		}
		referencesWithSpecifiedNamedMethod.add(classReference);
		
		ObjectSet<MethodInfo> methodsWithSameName = this.methodsByName.get(method.getName());
		if(methodsWithSameName == null) {
			methodsWithSameName = new ObjectSet<>();
			methodsByName.put(method.getName(), methodsWithSameName);
		}
		methodsWithSameName.add(info);
	}
	
	public void add(Object object, String objectID) {
		if(object == null) throw new IllegalArgumentException("Object is null.");
		if(objectID == null || objectID.isEmpty()) throw new IllegalArgumentException("Invalid object id");
		
		//Check if an instance reference is using the object as a reference
		ClassReference reference = getReference(object);
		
		classReferenceCheck:
		if(reference == null) {
			if(object.getClass().isAnnotationPresent(ConsoleObject.class)) {
				
				if(hasReference(objectID)) {
					ConsoleObject c = ClassReflection.getAnnotation(object.getClass(), ConsoleObject.class).getAnnotation(ConsoleObject.class);
					objectID = !c.value().isEmpty() ? c.value() : object.getClass().getSimpleName();
				}
				
				if(hasReference(objectID)) {
					logger.error("[Error]", "Could not create object");
					break classReferenceCheck;
				}
				
				addReference(object, objectID);
				reference = getReference(object);
			}
		}
		
		//Add methods with the ConsoleMethod annotation
		if(reference != null) {
			Method[] methods = ClassReflection.getMethods(object.getClass());
			
			for(Method m : methods) {
				if(!m.isPublic() || !m.isAnnotationPresent(ConsoleMethod.class)) continue;
				addMethodToCache(reference, m);
			}
		}
		
		//Add fields with the ConsoleReference annotation
		Field[] fields = ClassReflection.getFields(object.getClass());
		for(Field f : fields) {
			if(!f.isPublic() || !f.isAnnotationPresent(ConsoleObject.class)) continue;
			
			try {
				String name = f.getDeclaredAnnotation(ConsoleObject.class).getAnnotation(ConsoleObject.class).value();
				addReference(f.get(object), name);
			}
			catch(ReflectionException e) {
				logger.error(e.getMessage());
			}
		}
	}

}
