package com.vabrant.console;

import java.util.Iterator;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectMap.Values;
import com.badlogic.gdx.utils.ObjectSet;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.Field;
import com.badlogic.gdx.utils.reflect.Method;
import com.badlogic.gdx.utils.reflect.ReflectionException;
import com.vabrant.console.annotation.ConsoleMethod;
import com.vabrant.console.annotation.ConsoleReference;

public class ConsoleCache {
	
	public final static ConsoleCache GLOBAL_CACHE = null;
	
	private static final MethodInfo tempInfo = new MethodInfo();
	
	public final static String CLASS_REFERENCE_DESCRIPTION = "Reference:[%S] Name:[%s] Class:[%s] Full:[%s]";
	
	private ObjectMap<String, ClassReference> classReferences = new ObjectMap<>(20);
	private ObjectMap<String, ObjectSet<ClassReference>> referencesWithMethodName = new ObjectMap<>();
	private ObjectMap<ClassReference, ObjectSet<MethodInfo>> allMethodsOfReferences = new ObjectMap<>();
	private DebugLogger logger = new DebugLogger(ConsoleCache.class, DebugLogger.DEBUG);
	private final ClassMethodReference classMethodReference = new ClassMethodReference();
	
	public void setLogLevel(int level) {
		logger.setLevel(level);
		classMethodReference.logger.setLevel(level);
	}
	
	public ClassReference getReference(String name) {
		return classReferences.get(name);
	}
	
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
		ClassReference ref = classReferences.get(name);
		if(ref == null || !(ref instanceof InstanceReference)) return null;
		return (InstanceReference)ref;
	}
	
	public InstanceReference getInstanceReference(Object object) {
		if(object == null) return null;
		
		Values<ClassReference> values = classReferences.values();
		for(ClassReference r : values) {
			if(r instanceof InstanceReference) {
				InstanceReference ref = (InstanceReference)r;
				if(ref.getInstance().equals(object)) return ref;
			}
		}
		return null;
	}
	
	public boolean hasStaticReference(String name) {
		return getStaticReference(name) != null;
	}
	
	public StaticReference getStaticReference(String name) {
		if(name == null || name.isEmpty()) return null;
		ClassReference ref = classReferences.get(name);
		if(ref == null || !(ref instanceof StaticReference)) return null;
		return (StaticReference)ref;
	}
	
	public StaticReference getStaticReference(Class clazz) {
		if(clazz == null) return null;
		
		Values<ClassReference> values = classReferences.values();
		for(ClassReference r : values) {
			if(r instanceof StaticReference) {
				StaticReference ref = (StaticReference)r;
				if(ref.getReferenceClass().equals(clazz)) return ref;
			}
		}
		return null;
	}

	public boolean hasMethodWithName(String name) {
		return referencesWithMethodName.containsKey(name);
	}
	
	public boolean hasMethod(String referenceName, String methodName, Class... args) {
		ClassReference reference = getReference(referenceName);
		if(reference == null) return false;
		return hasMethod(reference, methodName, args);
	}
	
	public boolean hasMethod(ClassReference commandReference, String methodName, Class... args) {
		if(!hasMethodWithName(methodName)) return false;
		
		ObjectSet<MethodInfo> methodRefs = allMethodsOfReferences.get(commandReference);
		
		if(methodRefs == null) return false;
		
		Iterator<MethodInfo> it = methodRefs.iterator();
		while(it.hasNext()) {
			MethodInfo info = it.next();
			if(info.getName().equals(methodName) && ConsoleUtils.equals(info.getArgs(), ConsoleUtils.defaultIfNull(args, ConsoleUtils.EMPTY_ARGS))) return true;
		}
		return false;
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
			if(object.getClass().isAnnotationPresent(ConsoleReference.class)) {
				ConsoleReference o = ClassReflection.getAnnotation(object.getClass(), ConsoleReference.class).getAnnotation(ConsoleReference.class);
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
			if(clazz.isAnnotationPresent(ConsoleReference.class)) {
				ConsoleReference o = ClassReflection.getAnnotation(clazz, ConsoleReference.class).getAnnotation(ConsoleReference.class);
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

		Method method = null;
		
		try {
			method = ClassReflection.getMethod(object.getClass(), methodName, args);
		}
		catch(ReflectionException e) {
			logger.error(e.getMessage());
			return;
		}

		//Get the reference to the specified object
		InstanceReference instanceReference = getInstanceReference(object);
		
		//Creates a reference to the object if there isn't one
		if(instanceReference == null) {
			addReference(object, object.getClass().getSimpleName());
			instanceReference = getInstanceReference(object);
		}
		
		addMethodToCache(instanceReference, method);
	}
	
	public void addMethod(Class clazz, String methodName, Class... args) {
		if(clazz == null) throw new IllegalArgumentException("Class is null");
		if(methodName == null || methodName.isEmpty()) throw new IllegalArgumentException("Invalid method name");
		
		Method method = null;
		
		try {
			method = ClassReflection.getMethod(clazz, methodName, args);
			if(!method.isStatic()) throw new Exception("Method must be static");
		}
		catch(Exception e) {
			logger.error(e.getMessage());
			return;
		}
		
		ClassReference classReference = getStaticReference(clazz);
		
		if(classReference == null) {
			addReference(clazz, clazz.getSimpleName());
			classReference = getStaticReference(clazz);
		}
		
		addMethodToCache(classReference, method);
	}
	
	private void addMethodToCache(ClassReference reference, Method method) {
		ObjectSet<MethodInfo> allMethodsCallableByReference = allMethodsOfReferences.get(reference);
		if(allMethodsCallableByReference == null) {
			allMethodsCallableByReference = new ObjectSet<>();
			allMethodsOfReferences.put(reference, allMethodsCallableByReference);
		}
		
		tempInfo.set(method);

		//Check if the reference has the method
		Iterator<MethodInfo> it = allMethodsCallableByReference.iterator();
		while(it.hasNext()) {
			MethodInfo info = it.next();
			if(info.isEqual(tempInfo)) {
				logger.debug("Method already added");
				return;
			}
		}
		
		MethodInfo info = new MethodInfo();
		info.set(tempInfo);
		allMethodsCallableByReference.add(info);

		ObjectSet<ClassReference> referencesWithMethodName = this.referencesWithMethodName.get(method.getName());
		if(referencesWithMethodName == null) {
			referencesWithMethodName = new ObjectSet<ClassReference>();
			this.referencesWithMethodName.put(method.getName(), referencesWithMethodName);
		}
		
		referencesWithMethodName.add(reference);
		
		if(!classMethodReference.hasReferenceMethod(method)) classMethodReference.addReferenceMethod(method);
	}
	
	public void add(Object object, String objectID) {
		if(object == null) throw new IllegalArgumentException("Object is null.");
		if(objectID == null || objectID.isEmpty()) throw new IllegalArgumentException("Invalid object id");
		
		//Check if an instance reference is using the object as a reference
		InstanceReference instanceReference = getInstanceReference(object);
		
		classReferenceCheck:
		if(instanceReference == null) {
			if(object.getClass().isAnnotationPresent(ConsoleReference.class)) {
				
				if(hasReference(objectID)) {
					ConsoleReference c = ClassReflection.getAnnotation(object.getClass(), ConsoleReference.class).getAnnotation(ConsoleReference.class);
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
			if(!f.isPublic() || !f.isAnnotationPresent(ConsoleReference.class)) continue;
			
			try {
				String name = f.getDeclaredAnnotation(ConsoleReference.class).getAnnotation(ConsoleReference.class).value();
				addReference(f.get(object), name);
			}
			catch(ReflectionException e) {
				logger.error(e.getMessage());
			}
		}
	}

}
