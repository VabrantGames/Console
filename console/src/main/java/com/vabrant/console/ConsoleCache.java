package com.vabrant.console;

import java.util.Iterator;

import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectMap.Values;
import com.badlogic.gdx.utils.ObjectSet;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.Method;
import com.badlogic.gdx.utils.reflect.ReflectionException;
import com.vabrant.console.annotation.ConsoleObject;

public class ConsoleCache {
	
	private static final MethodInfo tempInfo = new MethodInfo();
	
	//(name) Class:SomeClass Full:com.test.SomeClass
	public final static String INSTANCE_DESCRIPTION = "(%s) Class:%s Full:%s";
	
	private ObjectMap<String, InstanceReference> instanceReferences = new ObjectMap<>(20);
	private ObjectMap<String, StaticReference> staticReferences = new ObjectMap<>(20);
//	private ObjectMap<String, ObjectSet<CommandReference>> methods = new ObjectMap<>(50);
//	ObjectMap<String, ObjectMap<String, ObjectSet<CommandReference>>> mm;
//	ObjectMap<String, ObjectMap<CommandReference, ObjectSet<MethodInfo>>> methods;
	ObjectMap<String, ObjectSet<CommandReference>> referencesWithMethodName;
	ObjectMap<CommandReference, ObjectSet<MethodInfo>> referenceMethods;
//	ObjectMap<CommandReference, ObjectSet<MethodReference>> commandReferenceMethods;
	private DebugLogger logger = new DebugLogger(Console.class, DebugLogger.DEBUG);
	private final ConsoleSettings settings;
	
	public ConsoleCache() {
		this(Console.getGlobalSettings());
	}
	
	public ConsoleCache(ConsoleSettings settings) {
		if(settings == null) throw new IllegalArgumentException("Settings is null.");
		this.settings = new ConsoleSettings(settings);
	}
	
	public InstanceReference getInstanceReference(String name) {
		return instanceReferences.get(name);
	}
	
	public InstanceReference getInstanceReference(Object instance) {
		if(instance == null) return null;
		
		Values<InstanceReference> values = instanceReferences.values();
		InstanceReference reference = null;
		while(values.hasNext()) {
			reference = values.next();
			if(reference.getInstance().equals(instance)) return reference;
		}
		return null;
	}
	
	public boolean hasInstanceReference(String name) {
		if(name == null) return false;
		return instanceReferences.containsKey(name);
	}
	
	public boolean hasInstanceReference(Object instance) {
		if(instance == null) return false;
		
		Values<InstanceReference> values = instanceReferences.values();
		InstanceReference reference = null;
		while(values.hasNext()) {
			reference = values.next();
			if(reference.getInstance().equals(instance)) return true;
		}
		return false;
	}
	
	public StaticReference getStaticReference(String name) {
		return staticReferences.get(name);
	}
	
	public boolean hasStaticReference(String name) {
		return staticReferences.containsKey(name);
	}

	public boolean hasMethodWithName(String name) {
		return referencesWithMethodName.containsKey(name);
	}
	
	public boolean hasMethod(String comandReferenceName, String methodName, Class... args) {
		return false;
	}
	
	public boolean hasMethod(CommandReference commandReference, String methodName, Class... args) {
		if(!hasMethodWithName(methodName)) return false;
		
		ObjectSet<MethodInfo> methodRefs = referenceMethods.get(commandReference);
		
		if(methodRefs == null) return false;
		
		Iterator<MethodInfo> it = methodRefs.iterator();
		while(it.hasNext()) {
			MethodInfo info = it.next();
			if(info.getName().equals(methodName) && ConsoleUtils.equals(info.getArgs(), ConsoleUtils.defaultIfNull(args, ConsoleUtils.EMPTY_ARGS))) return true;
		}
			
		return false;
	}
	
	public void addObject(Object object) {
		addObject(null, object);
	}
	
	/**
	 * Adds an object to the console.
	 * @param name Custom name for object. If null {@link java.lang.Class #getSimpleName object.getClass().getSimpleName()} will be used for the name.
	 * @param object Object to be saved.
	 */
	public void addObject(String name, Object object) {
		if(object == null) throw new IllegalArgumentException("Object is null");

		InstanceReference reference = getInstanceReference(object);
		
		//Check if a instance reference has the the same object
		if(reference != null) {
			logger.error(
					"Instance already exists", 
					String.format(
							INSTANCE_DESCRIPTION, 
							reference.getName(), 
							reference.getReferenceSimpleName(), 
							reference.getReferenceClass().getCanonicalName()),
					new Exception());
			return;
		}
		
		if(object.getClass().isAnnotationPresent(ConsoleObject.class)) {
			ConsoleObject o = ClassReflection.getAnnotation(object.getClass(), ConsoleObject.class).getAnnotation(ConsoleObject.class);
			
			//TODO Should name override the default name?
			name = o.value().isEmpty() ? object.getClass().getSimpleName() : o.value();
		}
		
		if(name == null || name.isEmpty()) name = object.getClass().getSimpleName();

		//Check if there's a instance reference that is using the given name
		reference = getInstanceReference(name);
		if(reference != null) {
			logger.error(
					"Name is already in use", 
					String.format(
							INSTANCE_DESCRIPTION, 
							reference.getName(), 
							reference.getReferenceSimpleName(), 
							reference.getReferenceClass().getCanonicalName()),
					new Exception());
			return;
		}

		reference = new InstanceReference(name, object);
		
		logger.info(
				"Added object", 
				String.format(
						INSTANCE_DESCRIPTION, 
						reference.getName(), 
						reference.getReferenceSimpleName(), 
						reference.getReferenceClass().getCanonicalName()));

		instanceReferences.put(name, reference);
	}
	
	//Adds a method of an object
	public void addMethod(String methodName, Object object, Class... args) {
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
		
		tempInfo.set(method);

		//Get the reference to the specified object
		InstanceReference instanceReference = getInstanceReference(object);
		
		//Creates a reference to the object if there isn't one
		if(instanceReference == null) {
			addObject(object.getClass().getSimpleName(), object);
			instanceReference = getInstanceReference(object);
		}
		
		ObjectSet<MethodInfo> allReferenceMethods = referenceMethods.get(instanceReference);

		if(allReferenceMethods == null) {
			allReferenceMethods = new ObjectSet<>();
			referenceMethods.put(instanceReference, allReferenceMethods);
		}

		//Return if the reference already has the method
		Iterator<MethodInfo> it = allReferenceMethods.iterator();
		while(it.hasNext()) {
			MethodInfo info = it.next();
			if(info.isEqual(tempInfo)) return;
		}
		
		MethodInfo info = new MethodInfo();
		info.set(tempInfo);
		allReferenceMethods.add(info);
		
		if(!ClassMethodReference.hasReferenceMethod(method)) ClassMethodReference.addReferenceMethod(method);
	}
	
	private void addMethod(String name, InstanceReference reference) {
		
	}
	
	public void add(Object object) {
		
	}
	
	public void add(Class klass) {
		
	}
	
	public static class MethodInfo implements Poolable {
		
		private String name;
		private Class declaringClass;
		private Class[] args;
		
		public void set(Method method) {
			this.name = method.getName();
			this.declaringClass = method.getDeclaringClass();
			this.args = method.getParameterTypes();
		}
		
		public void set(MethodInfo info) {
			name = info.getName();
			declaringClass = info.getDeclaringClass();
			args = info.getArgs();
		}
		
		public String getName() {
			return name;
		}
		
		public Class getDeclaringClass() {
			return declaringClass;
		}
		
		public Class[] getArgs() {
			return args;
		}

		public boolean isEqual(MethodInfo info) {
			if(!name.equals(info.getName()) || !ConsoleUtils.equals(args, info.getArgs())) return false;
			return true;
		}
		
		@Override
		public void reset() {
			name = null;
			declaringClass = null;
			args = null;
		}
		
	}
	

}
