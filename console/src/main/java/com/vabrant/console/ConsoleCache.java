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
	 
	private final ObjectMap<String, ClassReference<?>> classReferences = new ObjectMap<>(20);
	
	//Holds an array of references that share a method name
	private final ObjectMap<String, ObjectSet<ClassReference<?>>> referencesWithSpecifiedNamedMethod = new ObjectMap<>();
	
	//All methods belonging to a reference
	private final ObjectMap<ClassReference<?>, ObjectSet<MethodInfo>> methodsByReference = new ObjectMap<>();
	
	//Methods grouped by name
	private final ObjectMap<String, ObjectSet<MethodInfo>> methodsByName = new ObjectMap<>();
	private final ClassMethodReference classMethodReference = new ClassMethodReference();
	
	private final DebugLogger logger = new DebugLogger(ConsoleCache.class, DebugLogger.DEBUG);
	
	public void setLogLevel(int level) {
		logger.setLevel(level);
		classMethodReference.getLogger().setLevel(level);
	}
	
	/**
	 * Returns an instance or static reference from the specified name. Null is returned if no reference is found.
	 * @param name
	 * @return
	 */
	public ClassReference<?> getReference(String name) {
		return classReferences.get(name);
	}
	
	/**
	 * Checks if the cache contains an instance or static reference from the specified name.
	 * @param name
	 * @return
	 */
	public boolean hasReference(String name) {
		return classReferences.containsKey(name);
	}
	
	public boolean hasInstanceReference(String name) {
		return getInstanceReference(name) != null;
	}
	
	public boolean hasInstanceReference(Object object) {
		return getInstanceReference(object) != null;
	}
	
	public InstanceReference getInstanceReference(String name) {
		if(name == null || name.isEmpty()) return null;
		ClassReference<?> ref = classReferences.get(name);
		if(ref == null || !(ref instanceof InstanceReference)) return null;
		return (InstanceReference)ref;
	}
	
	public InstanceReference getInstanceReference(Object object) {
		if(object == null) return null;
		
		Values<ClassReference<?>> values = classReferences.values();
		for(ClassReference<?> r : values) {
			if(r instanceof InstanceReference) {
				InstanceReference instanceReference = (InstanceReference)r;
				if(instanceReference.getReference().equals(object)) return instanceReference;
			}
		}
		return null;
	}
	
	public boolean hasStaticReference(String name) {
		return getStaticReference(name) != null;
	}
	
	public boolean hasStaticReference(Class<?> clazz) {
		return getStaticReference(clazz) != null;
	}
	
	public StaticReference getStaticReference(String name) {
		if(name == null || name.isEmpty()) return null;
		ClassReference<?> ref = classReferences.get(name);
		if(ref == null || !(ref instanceof StaticReference)) return null;
		return (StaticReference)ref;
	}
	
	public StaticReference getStaticReference(Class<?> clazz) {
		if(clazz == null) return null;
		
		Values<ClassReference<?>> values = classReferences.values();
		for(ClassReference<?> r : values) {
			if(r instanceof StaticReference) {
				StaticReference ref = (StaticReference)r;
				if(ref.getReferenceClass().equals(clazz)) return ref;
			}
		}
		return null;
	}

	/**
	 * Check if a method exists.
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
		ClassReference <?>reference = getReference(referenceName);
		if(reference == null) return false;
		return hasMethod(reference, methodName, args);
	}
	
	public boolean hasMethod(ClassReference<?> classReference, String methodName, Class<?>... args) {
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
		ClassReference<?> reference = getReference(referenceName);
		if(reference == null) throw new RuntimeException("Reference " + referenceName + " not found.");
		
		ObjectSet<MethodInfo> methods = methodsByReference.get(reference);
		if(methods == null) throw new RuntimeException("Reference " + referenceName + " has 0 methods added.");
		return methods;
	}

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

		InstanceReference reference = getInstanceReference(object);
		
		//Check if an instance reference is using the object as a reference
		if(reference != null) {
			logger.debug(
					ConsoleUtils.CONFLICT_TAG + " (Reference already exists)", 
					String.format(
							CLASS_REFERENCE_DESCRIPTION, 
							"Instance",
							reference.getName(), 
							reference.getReferenceSimpleName(), 
							reference.getReferenceClass().getCanonicalName()));
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
			
			logger.debug(
					 ConsoleUtils.CONFLICT_TAG + " (Name is already in use)",  
					String.format(
							"UsedBy " +
							CLASS_REFERENCE_DESCRIPTION, 
							"Object",
							ref.getName(), 
							ref.getReferenceSimpleName(), 
							ref.getReferenceClass().getCanonicalName()));
			return;
		}

		reference = new InstanceReference(referenceID, object);
		
		logger.info(
				ConsoleUtils.ADDED_TAG, 
				String.format(
						CLASS_REFERENCE_DESCRIPTION,
						"Instance",
						reference.getName(), 
						reference.getReferenceSimpleName(), 
						reference.getReferenceClass().getCanonicalName()));

		classReferences.put(referenceID, reference);
	}
	
	public void addReference(Class clazz) {
		addReference(clazz, null);
	}
	
	/**
	 * Adds a class to the cache as a reference. This reference's purpose is to be used to call static methods. Maximum references for class references is one.
	 * References created with objects can call static methods if it or one its subclasses declares one. <br><br>
	 * 
	 * e.g. Adding the reference <br>
	 * <i>  addReference({@link MathUtils MathUtils.class}, "MathU"); </i><br>
	 * <i>  addMethod({@link MathUtils MathUtils.class}, "sin", float.class); </i><br><br>
	 * This will allow you to reference the <i>MathUtils</i> class and call static methods that you have added to the reference <br><br>
	 * 
	 * e.g Calling the reference <br>
	 * <i>reference.setRotation MathU.sin(20)
	 * 
	 * @param clazz Class used as a reference.
	 * @param referenceID Name used to call the reference.
	 */
	public void addReference(Class clazz, String referenceID) {
		if(clazz == null) throw new IllegalArgumentException("Class is null");
		
		ClassReference reference = getStaticReference(clazz);
		
		if(reference != null) {
			logger.debug(
					"[Conflict] (Reference already exists)",
					String.format(
							CLASS_REFERENCE_DESCRIPTION,
							"Static",
							reference.getName(),
							reference.getReferenceSimpleName(),
							reference.getReferenceClass().getCanonicalName()));
			return;
		}
		
		if(referenceID == null || referenceID.isEmpty()) {
			if(clazz.isAnnotationPresent(ConsoleObject.class)) {
				ConsoleObject o = ClassReflection.getAnnotation(clazz, ConsoleObject.class).getAnnotation(ConsoleObject.class);
				referenceID = !o.value().isEmpty() ? o.value() : clazz.getSimpleName();
			}
			else {
				referenceID = clazz.getSimpleName();
			}
		}
		
		//Check if an class reference is using the given name
		if(classReferences.containsKey(referenceID)) {
			ClassReference ref = getReference(referenceID);
			
			logger.debug(
					"[Conflict] (Name is already in use)",  
					String.format(
							"UsedBy " +
							CLASS_REFERENCE_DESCRIPTION, 
							"Static",
							ref.getName(), 
							ref.getReferenceSimpleName(), 
							ref.getReferenceClass().getCanonicalName()));
			return;
		}
		
		reference = new StaticReference(referenceID, clazz);
		
		logger.info(
				"[Added]", 
				String.format(
						CLASS_REFERENCE_DESCRIPTION,
						"Static",
						reference.getName(), 
						reference.getReferenceSimpleName(), 
						reference.getReferenceClass().getCanonicalName()));

		classReferences.put(referenceID, reference);
	}
	
	//Adds a method of an object
	public void addMethod(Object object, String methodName, Class... args) {
		if(object == null) throw new IllegalArgumentException("Object is null.");
		if(methodName == null || methodName.isEmpty()) throw new IllegalArgumentException("Invalid method name");

		//Check if the reference contains the same method
		InstanceReference instanceReference = getInstanceReference(object);
		if(instanceReference != null) {
			if(hasMethod(instanceReference, methodName, args)) return;
		}
		
		Method method = null;
		try {
			method = ClassReflection.getMethod(object.getClass(), methodName, args);
		}
		catch(ReflectionException e) {
			logger.error(e.getMessage());
			return;
		}

		//Get the reference to the specified object
		if(instanceReference == null) {
			addReference(object, object.getClass().getSimpleName());
			instanceReference = getInstanceReference(object);
		}
		
		addMethodToCache(instanceReference, method);
	}
	
	public void addMethod(Class clazz, String methodName, Class... args) {
		if(clazz == null) throw new IllegalArgumentException("Class is null");
		if(methodName == null || methodName.isEmpty()) throw new IllegalArgumentException("Invalid method name");
		
		StaticReference staticReference = getStaticReference(clazz);
		if(staticReference != null) {
			if(hasMethod(staticReference, methodName, args)) return;
		}
		
		Method method = null;
		
		try {
			method = ClassReflection.getMethod(clazz, methodName, args);
			if(!method.isStatic()) throw new Exception("Method must be static");
		}
		catch(Exception e) {
			logger.error(e.getMessage());
			return;
		}
		
		if(staticReference == null) {
			addReference(clazz, clazz.getSimpleName());
			staticReference = getStaticReference(clazz);
		}
		
		addMethodToCache(staticReference, method);
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

		ObjectSet<ClassReference<?>> referencesWithSpecifiedNamedMethod = this.referencesWithSpecifiedNamedMethod.get(method.getName());
		if(referencesWithSpecifiedNamedMethod == null) {
			referencesWithSpecifiedNamedMethod = new ObjectSet<ClassReference<?>>();
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
		InstanceReference instanceReference = getInstanceReference(object);
		
		classReferenceCheck:
		if(instanceReference == null) {
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
				instanceReference = getInstanceReference(object);
			}
		}
		
		//Add methods with the ConsoleMethod annotation
		if(instanceReference != null) {
			Method[] methods = ClassReflection.getMethods(object.getClass());
			
			for(Method m : methods) {
				if(!m.isPublic() || !m.isAnnotationPresent(ConsoleMethod.class)) continue;
				addMethodToCache(instanceReference, m);
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
