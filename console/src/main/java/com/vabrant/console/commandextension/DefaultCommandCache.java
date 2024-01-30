
package com.vabrant.console.commandextension;

import com.badlogic.gdx.utils.Logger;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectMap.Values;
import com.badlogic.gdx.utils.ObjectSet;
import com.badlogic.gdx.utils.reflect.*;
import com.badlogic.gdx.utils.StringBuilder;
import com.vabrant.console.ConsoleRuntimeException;
import com.vabrant.console.DebugLogger;
import com.vabrant.console.commandextension.annotation.ConsoleCommand;
import com.vabrant.console.commandextension.annotation.ConsoleReference;
import com.vabrant.console.gui.shortcuts.*;

public class DefaultCommandCache implements CommandCache {

	private static final String CONFLICT_NAME_IN_USE_TAG = "[Conflict] (Name already in use)";
	private static final String CONFLICT_REFERENCE_EXISTS_TAG = "[Conflict] (Reference already exists)";
	private static final String NAME_TAG = " [Name]:";
	private static final String CLASS_TAG = " [Class]:";
	private static final String FULL_TAG = " [Full]:";
	private static final String ADDED_TAG = "[Added]";

	// References by name
	private final ObjectMap<String, ClassReference<?>> classReferences;

	// Commands for reference
	private final ObjectMap<ClassReference<?>, ObjectSet<Command>> commandsByReference;

	// Commands grouped by name
	private final ObjectMap<String, ObjectSet<Command>> commandsByName;

	private DefaultKeyMap keyMap;

	private final DebugLogger logger;
	private final StringBuilder stringBuilder;

	public DefaultCommandCache () {
		keyMap = new DefaultKeyMap(GUIConsoleShortcutManager.GLOBAL_SCOPE);
		classReferences = new ObjectMap<>();
		commandsByReference = new ObjectMap<>();
		commandsByName = new ObjectMap<>();
		logger = new DebugLogger(this.getClass().getSimpleName(), DebugLogger.NONE);
		stringBuilder = new StringBuilder(200);
	}

	@Override
	public DebugLogger getLogger () {
		return logger;
	}

	@Override
	public boolean hasReference (String name) {
		return classReferences.containsKey(name);
	}

	@Override
	public boolean hasReference (Object object) {
		return getReference(object) != null;
	}

	@Override
	public ClassReference getReference (String name) {
		return classReferences.get(name);
	}

	@Override
	public ClassReference getReference (Object object) {
		if (object == null) return null;

		Values<ClassReference<?>> values = classReferences.values();
		for (ClassReference<?> r : values) {
			if (r.getReference().equals(object)) return r;
		}

		return null;
	}

	@Override
	public boolean hasCommandWithName (String name) {
		return hasCommandWithName((ClassReference<?>)null, name);
	}

	public boolean hasCommandWithName (String referenceName, String name) {
		return hasCommandWithName(getReference(referenceName), name);
	}

	@Override
	public boolean hasCommandWithName (ClassReference<?> classReference, String name) {
		if (classReference == null) {
			return commandsByName.get(name) != null;
		} else {
			ObjectSet<Command> commands = commandsByReference.get(classReference);

			if (commands == null) {
				return false;
			}

			for (Command c : commands) {
				if (c.getMethodName().equals(name)) return true;
			}
		}
		return false;
	}

	@Override
	public boolean hasCommand (String name, Class<?>... args) {
		return hasCommand((ClassReference<?>)null, name, args);
	}

	public boolean hasCommand (String referenceName, String name, Class<?>... args) {
		return hasCommand(getReference(referenceName), name, args);
	}

	@Override
	public boolean hasCommand (ClassReference<?> classReference, String name, Class<?>... args) {
		return getCommand(classReference, name, args) != null;
	}

	@Override
	public Command getCommand (String commandName, Class<?>... args) {
		return getCommand((ClassReference<?>)null, commandName, args);
	}

	public Command getCommand (String referenceName, String commandName, Class<?>... args) {
		return getCommand(getReference(referenceName), commandName, args);
	}

	@Override
	public Command getCommand (ClassReference<?> classReference, String commandName, Class<?>... args) {
		if (commandName == null) return null;

		ObjectSet<Command> commands;

		if (classReference == null) {
			commands = commandsByName.get(commandName);

			if (commands == null) return null;

			for (Command c : commands) {
				if (CommandUtils.areArgsEqual(c.getArgs(), args)) return c;
			}
		} else {
			commands = commandsByReference.get(classReference);

			if (commands == null) {
				return null;
			}

			for (Command c : commands) {
				if (c.getMethodName().equals(commandName)
					&& CommandUtils.areArgsEqual(c.getArgs(), CommandUtils.defaultIfNull(args, CommandUtils.EMPTY_ARGUMENT_TYPES)))
					return c;
			}
		}
		return null;
	}

	@Override
	public ObjectSet<Command> getAllCommandsWithName (String name) {
		return commandsByName.get(name);
	}

	public ObjectSet<Command> getAllCommandsForReference (String referenceName) {
		return getAllCommandsForReference(getReference(referenceName));
	}

	@Override
	public ObjectSet<Command> getAllCommandsForReference (ClassReference<?> reference) {
		if (reference == null) throw new IllegalArgumentException("Reference is null.");
		return commandsByReference.get(reference);
	}

	public ClassReference addReference (Object object) {
		return addReference(object, null);
	}

	@Override
	public ClassReference addReference (Object object, String referenceID) {
		if (object == null) throw new IllegalArgumentException("Reference object is null");

		boolean isStatic = object instanceof Class;
		ClassReference<?> reference = getReference(object);

		// Check if the object is already being used as a reference
		if (reference != null) {
			throw new ConsoleRuntimeException("Object already in use.");
		}

		if (referenceID == null || referenceID.isEmpty()) {
			if (object.getClass().isAnnotationPresent(ConsoleReference.class)) {
				ConsoleReference o = ClassReflection.getAnnotation(object.getClass(), ConsoleReference.class)
					.getAnnotation(ConsoleReference.class);
				referenceID = o.value();
			}

			if (referenceID == null || referenceID.isEmpty()) {
				throw new ConsoleRuntimeException("Reference ID can't be null or empty");
			}
		}

		// Check if a class reference is using the given name
		if (classReferences.containsKey(referenceID)) {
			throw new ConsoleRuntimeException("Reference ID already being used");
		}

		if (isStatic) {
			reference = new StaticReference(referenceID, (Class<?>)object);
		} else {
			reference = new InstanceReference(referenceID, object);
		}

		if (logger.getLevel() >= Logger.INFO) {
			stringBuilder.clear();
			stringBuilder.append(ADDED_TAG);

			if (isStatic) {
				stringBuilder.append(" [Reference]:Static");
			} else {
				stringBuilder.append(" [Reference]:Instance");
			}

			stringBuilder.append(NAME_TAG);
			stringBuilder.append(reference.getName());
			stringBuilder.append(CLASS_TAG);
			stringBuilder.append(reference.getReferenceSimpleName());
			stringBuilder.append(FULL_TAG);
			stringBuilder.append(reference.getReferenceClass().getCanonicalName());
			logger.info(stringBuilder.toString());
		}

		classReferences.put(referenceID, reference);

		return reference;
	}

	@Override
	public void addCommand (ClassReference<?> classReference, String methodName, Class<?>... args) {
		if (classReference == null) throw new IllegalArgumentException("Class reference is null.");
		if (methodName == null || methodName.isEmpty()) throw new IllegalArgumentException("Invalid method name");

		boolean isStatic = classReference instanceof StaticReference;

		Method method = null;
		try {
			method = ClassReflection.getMethod(classReference.getReferenceClass(), methodName, args);

			if (!method.isPublic()) throw new ConsoleRuntimeException("Method must be public");
			if (isStatic && !method.isStatic())
				throw new ConsoleRuntimeException("Static references can only contain static methods");
		} catch (Exception e) {
			throw new ConsoleRuntimeException(e.getMessage(), e);
		}

		addCommandToCache(classReference, method);
	}

	@Override
	public void addAll (ClassReference<?> classReference) {
		if (classReference == null) throw new IllegalArgumentException("Class reference is null.");

		// If the reference has the ConsoleReference annotation looks for methods that have
		// ConsoleCommand annotation
		if (classReference.getReferenceClass().isAnnotationPresent(ConsoleReference.class)) {
			Method[] methods = ClassReflection.getMethods(classReference.getReferenceClass());

			for (Method m : methods) {
				if (!m.isPublic() || !m.isAnnotationPresent(ConsoleCommand.class)) continue;
				addCommandToCache(classReference, m);
			}
		}

		// Add fields with the ConsoleReference annotation
		Field[] fields = ClassReflection.getFields(classReference.getReferenceClass());
		for (Field f : fields) {
			if (!f.isPublic() || !f.isAnnotationPresent(ConsoleReference.class)) continue;

			try {
				String name = f.getDeclaredAnnotation(ConsoleReference.class).getAnnotation(ConsoleReference.class).value();
				addReference(f.get(classReference.getReference()), name);
			} catch (ReflectionException e) {
				logger.error(e.getMessage());
			}
		}
	}

	private void addCommandToCache (ClassReference<?> classReference, Method method) {
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

}
