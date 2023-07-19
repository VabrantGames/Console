
package com.vabrant.console.commandextension;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Logger;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectMap.Values;
import com.badlogic.gdx.utils.ObjectSet;
import com.badlogic.gdx.utils.reflect.*;
import com.badlogic.gdx.utils.StringBuilder;
import com.vabrant.console.DebugLogger;
import com.vabrant.console.commandextension.annotation.ConsoleCommand;
import com.vabrant.console.commandextension.annotation.ConsoleReference;

public class CommandCache {

	private static final String CONFLICT_NAME_IN_USE_TAG = "[Conflict] (Name already in use)";
	private static final String CONFLICT_REFERENCE_EXISTS_TAG = "[Conflict] (Reference already exists)";
	private static final String NAME_TAG = " [Name]:";
	private static final String CLASS_TAG = " [Class]:";
	private static final String FULL_TAG = " [Full]:";
	private static final String ADDED_TAG = "[Added]";

	// References by name
	private final ObjectMap<String, ClassReference<?>> classReferences = new ObjectMap<>(20);

	// Commands for reference
	private final ObjectMap<ClassReference<?>, ObjectSet<Command>> commandsByReference = new ObjectMap<>();

	// Commands grouped by name
	private final ObjectMap<String, ObjectSet<Command>> commandsByName = new ObjectMap<>();

	private final DebugLogger logger = new DebugLogger(CommandCache.class.getSimpleName(), DebugLogger.NONE);
	private final StringBuilder stringBuilder = new StringBuilder(150);

	public DebugLogger getLogger () {
		return logger;
	}

	/** Returns an instance or static reference from the specified name. Null is returned if no reference is found.
	 *
	 * @param name
	 * @return */
	public ClassReference getReference (String name) {
		return classReferences.get(name);
	}

	/** Checks if the cache contains an instance or static reference from the specified name.
	 *
	 * @param name
	 * @return */
	public boolean hasReference (String name) {
		return classReferences.containsKey(name);
	}

	public boolean hasInstanceReference (String name) {
		return getInstanceReference(name) != null;
	}

	public boolean hasInstanceReference (Object object) {
		return getInstanceReference(object) != null;
	}

	public InstanceReference getInstanceReference (String name) {
		if (name == null || name.isEmpty()) return null;
		ClassReference<?> ref = classReferences.get(name);
		if (!(ref instanceof InstanceReference)) return null;
		return (InstanceReference)ref;
	}

	public InstanceReference getInstanceReference (Object object) {
		if (object == null) return null;
		Values<ClassReference<?>> values = classReferences.values();
		for (ClassReference<?> r : values) {
			if (r instanceof InstanceReference) {
				InstanceReference instanceReference = (InstanceReference)r;
				if (instanceReference.getReference().equals(object)) return instanceReference;
			}
		}
		return null;
	}

	public boolean hasStaticReference (String name) {
		return getStaticReference(name) != null;
	}

	public boolean hasStaticReference (Class<?> clazz) {
		return getStaticReference(clazz) != null;
	}

	public StaticReference getStaticReference (String name) {
		if (name == null || name.isEmpty()) return null;
		ClassReference<?> ref = classReferences.get(name);
		if (!(ref instanceof StaticReference)) return null;
		return (StaticReference)ref;
	}

	public StaticReference getStaticReference (Class<?> clazz) {
		if (clazz == null) return null;
		Values<ClassReference<?>> values = classReferences.values();
		for (ClassReference<?> r : values) {
			if (r instanceof StaticReference) {
				StaticReference ref = (StaticReference)r;
				if (ref.getReferenceClass().equals(clazz)) return ref;
			}
		}
		return null;
	}

	/** Check if a method exists.
	 *
	 * @param name
	 * @return */
	public boolean hasCommandWithName (String name) {
		return commandsByName.get(name) != null;
	}

	public boolean hasCommandWithName (String referenceName, String name) {
		return hasCommandWithName(getReference(referenceName), name);
	}

	public boolean hasCommandWithName (ClassReference<?> classReference, String name) {
		ObjectSet<Command> commands = commandsByReference.get(classReference);

		if (commands == null) return false;

		for (Command c : commands) {
			if (c.getMethodName().equals(name)) return true;
		}
		return false;
	}

	public boolean hasCommand (String name, Class<?>... args) {
		ObjectSet<Command> commands = commandsByName.get(name);

		if (commands == null) return false;

		for (Command m : commands) {
			if (CommandUtils.areArgsEqual(m.getArgs(), args)) return true;
		}
		return false;
	}

	/** Check if a reference has a method added.
	 *
	 * @param referenceName
	 * @param name
	 * @param args
	 * @return */
	public boolean hasCommand (String referenceName, String name, Class<?>... args) {
		return hasCommand(getReference(referenceName), name, args);
	}

	public boolean hasCommand (Object reference, String name, Class<?>... args) {
		return hasCommand(getInstanceReference(reference), name, args);
	}

	public boolean hasCommand (ClassReference<?> classReference, String name, Class<?>... args) {
		if (classReference == null || name == null) return false;

		ObjectSet<Command> commands = commandsByReference.get(classReference);

		if (commands == null) return false;

		for (Command c : commands) {
			if (c.getMethodName().equals(name)
				&& CommandUtils.areArgsEqual(c.getArgs(), CommandUtils.defaultIfNull(args, CommandUtils.EMPTY_ARGUMENT_TYPES)))
				return true;
		}
		return false;
	}

	public ObjectSet<Command> getAllCommandsWithName (String name) {
		return commandsByName.get(name);
	}

	public ObjectSet<Command> getAllCommandsByReference (String referenceName) {
		ClassReference<?> reference = getReference(referenceName);
		if (reference == null) throw new RuntimeException("Reference " + referenceName + " not found.");

		ObjectSet<Command> commands = commandsByReference.get(reference);
		if (commands == null) throw new RuntimeException("Reference " + referenceName + " has 0 methods added.");
		return commands;
	}

	public void addReference (Object object) {
		addReference(object, null);
	}

	/** Adds an object to the cache as a reference. This same object can have methods added to it later.
	 *
	 * @param object Object used as a reference.
	 * @param referenceID Name used to call the reference. */
	public void addReference (Object object, String referenceID) {
		if (object == null) throw new IllegalArgumentException("Object is null");

		InstanceReference reference = getInstanceReference(object);

		// Check if an instance reference is using the object as a reference
		if (reference != null) {
			if (logger.getLevel() >= Logger.ERROR) {
				stringBuilder.clear();
				stringBuilder.append(CONFLICT_REFERENCE_EXISTS_TAG).append(" [Reference]:Instance").append(NAME_TAG)
					.append(reference.getName()).append(CLASS_TAG).append(reference.getReferenceSimpleName()).append(FULL_TAG)
					.append(reference.getReferenceClass().getCanonicalName());
				logger.error(stringBuilder.toString());
			}
			return;
		}

		if (referenceID == null || referenceID.isEmpty()) {
			if (object.getClass().isAnnotationPresent(ConsoleReference.class)) {
				ConsoleReference o = ClassReflection.getAnnotation(object.getClass(), ConsoleReference.class)
					.getAnnotation(ConsoleReference.class);
				referenceID = !o.value().isEmpty() ? o.value() : object.getClass().getSimpleName();
			} else {
				referenceID = object.getClass().getSimpleName();
			}
		}

		// Check if an class reference is using the given name
		if (classReferences.containsKey(referenceID)) {
			if (logger.getLevel() >= Logger.ERROR) {
				ClassReference ref = getReference(referenceID);
				stringBuilder.clear();
				stringBuilder.append(CONFLICT_NAME_IN_USE_TAG);
				stringBuilder.append(" [Reference]:Object");
				stringBuilder.append(NAME_TAG);
				stringBuilder.append(ref.getName());
				stringBuilder.append(CLASS_TAG);
				stringBuilder.append(ref.getReferenceSimpleName());
				stringBuilder.append(FULL_TAG);
				stringBuilder.append(ref.getReferenceClass().getCanonicalName());
				logger.error(stringBuilder.toString());
			}
			return;
		}

		reference = new InstanceReference(referenceID, object);

		if (logger.getLevel() >= Logger.INFO) {
			stringBuilder.clear();
			stringBuilder.append(ADDED_TAG);
			stringBuilder.append(" [Reference]:Instance");
			stringBuilder.append(NAME_TAG);
			stringBuilder.append(reference.getName());
			stringBuilder.append(CLASS_TAG);
			stringBuilder.append(reference.getReferenceSimpleName());
			stringBuilder.append(FULL_TAG);
			stringBuilder.append(reference.getReferenceClass().getCanonicalName());
			logger.info(stringBuilder.toString());
		}

		classReferences.put(referenceID, reference);
	}

	public void addReference (Class clazz) {
		addReference(clazz, null);
	}

	/** Adds a class to the cache as a reference. This reference's purpose is to be used to call static methods. Maximum references
	 * for class references is one. References created with objects can call static methods if it or one its subclasses declares
	 * one. <br>
	 * <br>
	 * <p>
	 * e.g. Adding the reference <br>
	 * <i> addReference({@link MathUtils MathUtils.class}, "MathU"); </i><br>
	 * <i> addMethod({@link MathUtils MathUtils.class}, "sin", float.class); </i><br>
	 * <br>
	 * This will allow you to reference the <i>MathUtils</i> class and call static methods that you have added to the reference
	 * <br>
	 * <br>
	 * <p>
	 * e.g Calling the reference <br>
	 * <i>reference.setRotation MathU.sin(20)
	 *
	 * @param clazz Class used as a reference.
	 * @param referenceID Name used to call the reference. */
	public void addReference (Class clazz, String referenceID) {
		if (clazz == null) throw new IllegalArgumentException("Class is null");

		ClassReference reference = getStaticReference(clazz);

		if (reference != null) {
			if (logger.getLevel() >= Logger.ERROR) {
				stringBuilder.clear();
				stringBuilder.append(CONFLICT_REFERENCE_EXISTS_TAG);
				stringBuilder.append(" [Reference]:Static");
				stringBuilder.append(NAME_TAG);
				stringBuilder.append(reference.getName());
				stringBuilder.append(CLASS_TAG);
				stringBuilder.append(reference.getReferenceSimpleName());
				stringBuilder.append(FULL_TAG);
				stringBuilder.append(reference.getReferenceClass().getCanonicalName());
				logger.error(stringBuilder.toString());
			}
			return;
		}

		if (referenceID == null || referenceID.isEmpty()) {
			if (clazz.isAnnotationPresent(ConsoleReference.class)) {
				ConsoleReference o = ClassReflection.getAnnotation(clazz, ConsoleReference.class)
					.getAnnotation(ConsoleReference.class);
				referenceID = !o.value().isEmpty() ? o.value() : clazz.getSimpleName();
			} else {
				referenceID = clazz.getSimpleName();
			}
		}

		// Check if a class reference is using the given name
		if (classReferences.containsKey(referenceID)) {
			if (logger.getLevel() >= Logger.ERROR) {
				reference = getReference(referenceID);
				stringBuilder.clear();
				stringBuilder.append(CONFLICT_NAME_IN_USE_TAG);
				stringBuilder.append(" [Reference]:Static");
				stringBuilder.append(NAME_TAG);
				stringBuilder.append(reference.getName());
				stringBuilder.append(CLASS_TAG);
				stringBuilder.append(reference.getReferenceSimpleName());
				stringBuilder.append(FULL_TAG);
				stringBuilder.append(reference.getReferenceClass().getCanonicalName());
				logger.error(stringBuilder.toString());
			}
			return;
		}

		reference = new StaticReference(referenceID, clazz);

		if (logger.getLevel() >= Logger.INFO) {
			stringBuilder.clear();
			stringBuilder.append(ADDED_TAG);
			stringBuilder.append(" [Reference]:Static");
			stringBuilder.append(NAME_TAG);
			stringBuilder.append(reference.getName());
			stringBuilder.append(CLASS_TAG);
			stringBuilder.append(reference.getReferenceSimpleName());
			stringBuilder.append(FULL_TAG);
			stringBuilder.append(reference.getReferenceClass().getCanonicalName());
			logger.info(stringBuilder.toString());
		}

		classReferences.put(referenceID, reference);
	}

	public void addCommand (Object object, String methodName, Class... args) {
		if (object == null) throw new IllegalArgumentException("Object is null.");
		if (methodName == null || methodName.isEmpty()) throw new IllegalArgumentException("Invalid method name");

		// Check if the reference contains the same method
		InstanceReference instanceReference = getInstanceReference(object);
		if (instanceReference != null) {
			if (hasCommand(instanceReference, methodName, args)) return;
		}

		Method method = null;
		try {
			method = ClassReflection.getMethod(object.getClass(), methodName, args);
		} catch (ReflectionException e) {
			logger.error(e.getMessage());
			return;
		}

		// Get the reference to the specified object
		if (instanceReference == null) {
			addReference(object, object.getClass().getSimpleName());
			instanceReference = getInstanceReference(object);
		}

		addCommandToCache(instanceReference, method);
	}

	public void addCommand (Class clazz, String methodName, Class... args) {
		if (clazz == null) throw new IllegalArgumentException("Class is null");
		if (methodName == null || methodName.isEmpty()) throw new IllegalArgumentException("Invalid method name");

		StaticReference staticReference = getStaticReference(clazz);
		if (staticReference != null) {
			if (hasCommand(staticReference, methodName, args)) return;
		}

		Method method = null;

		try {
			method = ClassReflection.getMethod(clazz, methodName, args);
			if (!method.isStatic()) throw new Exception("Method must be static");
		} catch (Exception e) {
			logger.error(e.getMessage());
			return;
		}

		if (staticReference == null) {
			addReference(clazz, clazz.getSimpleName());
			staticReference = getStaticReference(clazz);
		}

		addCommandToCache(staticReference, method);
	}

	private void addCommandToCache (ClassReference classReference, Method method) {
		Command command = new Command(classReference, method);

		if (method.isAnnotationPresent(ConsoleCommand.class)) {
			ConsoleCommand annotation = method.getDeclaredAnnotation(ConsoleCommand.class).getAnnotation(ConsoleCommand.class);
			command.setSuccessMessage(annotation.successMessage());
		}

		// Get a set containing all added methods for the reference
		ObjectSet<Command> allMethodsForReference = commandsByReference.get(classReference);
		if (allMethodsForReference == null) {
			allMethodsForReference = new ObjectSet<>();
			commandsByReference.put(classReference, allMethodsForReference);
		}
		allMethodsForReference.add(command);

		ObjectSet<Command> methodsWithSameName = this.commandsByName.get(method.getName());
		if (methodsWithSameName == null) {
			methodsWithSameName = new ObjectSet<>();
			commandsByName.put(method.getName(), methodsWithSameName);
		}
		methodsWithSameName.add(command);

		if (logger.getLevel() > 0) {
			stringBuilder.clear();
			stringBuilder.append("[New] [Command] ");

			if (logger.getLevel() == DebugLogger.DEBUG) {
				stringBuilder.append("").append(method.getName());
				stringBuilder.append(" Return:").append(command.getReturnType().getCanonicalName());
				stringBuilder.append(" Args:").append(CommandUtils.argsToString(command.getArgs()));
				stringBuilder.append(" Class:").append(command.getDeclaringClass().getSimpleName());
				stringBuilder.append(" Full:").append(command.getDeclaringClass().getCanonicalName());
			} else {
				stringBuilder.append(command.toString());
			}

			logger.info(stringBuilder.toString());
		}
	}

	public void addAll (Object object, String objectID) {
		if (object == null) throw new IllegalArgumentException("Object is null.");
		if (objectID == null || objectID.isEmpty()) throw new IllegalArgumentException("Invalid object id");

		// Check if an instance reference is using the object as a reference
		InstanceReference instanceReference = getInstanceReference(object);

		classReferenceCheck:
		if (instanceReference == null) {
			if (object.getClass().isAnnotationPresent(ConsoleReference.class)) {

				if (hasReference(objectID)) {
					ConsoleReference c = ClassReflection.getAnnotation(object.getClass(), ConsoleReference.class)
						.getAnnotation(ConsoleReference.class);
					objectID = !c.value().isEmpty() ? c.value() : object.getClass().getSimpleName();
				}

				if (hasReference(objectID)) {
					logger.error("[Error]" + "Could not create object");
					break classReferenceCheck;
				}

				addReference(object, objectID);
				instanceReference = getInstanceReference(object);
			}
		}

		// Add methods with the ConsoleMethod annotation
		if (instanceReference != null) {
			Method[] methods = ClassReflection.getMethods(object.getClass());

			for (Method m : methods) {
				if (!m.isPublic() || !m.isAnnotationPresent(ConsoleCommand.class)) continue;
				addCommandToCache(instanceReference, m);
			}
		}

		// Add fields with the ConsoleReference annotation
		Field[] fields = ClassReflection.getFields(object.getClass());
		for (Field f : fields) {
			if (!f.isPublic() || !f.isAnnotationPresent(ConsoleReference.class)) continue;

			try {
				String name = f.getDeclaredAnnotation(ConsoleReference.class).getAnnotation(ConsoleReference.class).value();
				addReference(f.get(object), name);
			} catch (ReflectionException e) {
				logger.error(e.getMessage());
			}
		}
	}

}
