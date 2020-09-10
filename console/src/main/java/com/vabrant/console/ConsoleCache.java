package com.vabrant.console;

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
	
	public static class ConsoleCacheClassAdndMethodReference {
		private final ClassReference valueOne;
		private final MethodReference valueTwo;
		
		public ConsoleCacheClassAdndMethodReference(ClassReference valueOne, MethodReference valueTwo) {
			this.valueOne = valueOne;
			this.valueTwo = valueTwo;
		}
		
		public ClassReference getClassReference() {
			return valueOne;
		}
		
		public MethodReference getMethodReference() {
			return valueTwo;
		}
	}
	
	public final static ConsoleCache GLOBAL_CACHE = null;
	public final static String CLASS_REFERENCE_DESCRIPTION = "Reference:[%S] Name:[%s] Class:[%s] Full:[%s]";
	 
	private final ObjectMap<String, ClassReference> classReferences = new ObjectMap<>(20);

	private final ObjectMap<String, ObjectSet<ConsoleCacheClassAdndMethodReference>> methodsByName = new ObjectMap<>();
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
	public ClassReference getClassReference(String name) {
		return classReferences.get(name);
	}
	
	/**
	 * Returns a class reference for the specified object. Null is returned if no reference is found.
	 * @param name
	 * @return
	 */
	public ClassReference getClassReference(Object object) {
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
	public boolean hasClassReference(String name) {
		return classReferences.containsKey(name);
	}
	
	public boolean hasClassReference(Object object) {
		return getClassReference(object) != null;
	}

	/**
	 * Check if a method exists for the specified name.
	 * @param name
	 * @return
	 */
	public boolean hasMethod(String name) {
		return methodsByName.containsKey(name);
	}
	
	/**
	 * Check if a reference has a method added.
	 * @param referenceName
	 * @param methodName
	 * @param args
	 * @return
	 */
	public boolean hasMethod(String referenceName, String methodName, Class<?>... args) {
		ClassReference reference = getClassReference(referenceName);
		if(reference == null) return false;
		return hasMethod(reference, methodName, args);
	}
	
	public boolean hasMethod(ClassReference classReference, String methodName, Class<?>... args) {
		if(!hasMethod(methodName)) return false;
		
		ObjectSet<MethodReference> methods = classReference.getMethodReferences();
		if(methods == null) return false;
		
		for(MethodReference reference : methods) {
			if(reference.isEqual(methodName, args)) return true;
		}
		return false;
	}
	
	public ObjectSet<ConsoleCacheClassAdndMethodReference> getAllMethodsWithName(String name){
		ObjectSet<ConsoleCacheClassAdndMethodReference> methods = methodsByName.get(name);
		if(methods == null) throw new RuntimeException("Method [" + name + "] not found.");
		return methods;
	}
	
	public ObjectSet<MethodReference> getAllMethodsByReference(String referenceName){
		return getAllMethodsByReference(getClassReference(referenceName));
	}
	
	public ObjectSet<MethodReference> getAllMethodsByReference(ClassReference reference) {
		if(reference == null) throw new IllegalArgumentException("Reference not found");
		
		ObjectSet<MethodReference> methods = reference.getMethodReferences();
		if(methods == null) throw new RuntimeException("Reference " + reference.getName() + " has 0 methods added.");
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

		ClassReference reference = getClassReference(object);
		
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
			ClassReference ref = getClassReference(referenceID);

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
		ClassReference reference = getClassReference(object);
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
			reference = getClassReference(object);
		}
		
		addMethodToCache(reference, method);
	}
	
	private void addMethodToCache(ClassReference classReference, Method method) {
		MethodReference methodReference = classMethodReference.getReferenceMethod(method);
		if(methodReference == null) {
			methodReference = classMethodReference.addReferenceMethod(method);
		}
		
		//Add the reference to the ClassReference
		classReference.addMethodReference(methodReference);

		ObjectSet<ConsoleCacheClassAdndMethodReference> entries = methodsByName.get(method.getName());
		if(entries == null) {
			entries = new ObjectSet<>();
			methodsByName.put(method.getName(), entries);
		}
		entries.add(new ConsoleCacheClassAdndMethodReference(classReference, methodReference));
	}
	
	public void add(Object object, String objectID) {
		if(object == null) throw new IllegalArgumentException("Object is null.");
		if(objectID == null || objectID.isEmpty()) throw new IllegalArgumentException("Invalid object id");
		
		//Check if an instance reference is using the object as a reference
		ClassReference reference = getClassReference(object);
		
		classReferenceCheck:
		if(reference == null) {
			if(object.getClass().isAnnotationPresent(ConsoleObject.class)) {
				
				if(hasClassReference(objectID)) {
					ConsoleObject c = ClassReflection.getAnnotation(object.getClass(), ConsoleObject.class).getAnnotation(ConsoleObject.class);
					objectID = !c.value().isEmpty() ? c.value() : object.getClass().getSimpleName();
				}
				
				if(hasClassReference(objectID)) {
					logger.error("[Error]", "Could not create object");
					break classReferenceCheck;
				}
				
				addReference(object, objectID);
				reference = getClassReference(object);
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
