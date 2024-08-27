
package com.vabrant.console.CommandEngine;

import com.badlogic.gdx.utils.Logger;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectSet;
import com.badlogic.gdx.utils.StringBuilder;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.Field;
import com.badlogic.gdx.utils.reflect.Method;
import com.badlogic.gdx.utils.reflect.ReflectionException;
import com.vabrant.console.ConsoleRuntimeException;
import com.vabrant.console.DebugLogger;
import com.vabrant.console.Utils;

public class DefaultCommandCache implements CommandCache {

	private static final String CONFLICT_NAME_IN_USE_TAG = "[Conflict] (Name already in use)";
	private static final String CONFLICT_REFERENCE_EXISTS_TAG = "[Conflict] (Reference already exists)";
	private static final String NAME_TAG = " [Name]:";
	private static final String CLASS_TAG = " [Class]:";
	private static final String FULL_TAG = " [Full]:";
	private static final String ADDED_TAG = "[Added]";
	private static final Class[] emptyArgTypes = new Class[0];

	// References by name
	private final ObjectMap<String, ClassReference<?>> references;

	// Commands grouped by name
	private final ObjectMap<String, ObjectSet<Command>> commands;

	private final DebugLogger logger;
	private final StringBuilder stringBuilder;

	public DefaultCommandCache () {
		references = new ObjectMap<>();
		commands = new ObjectMap<>();
		logger = new DebugLogger(this.getClass().getSimpleName(), DebugLogger.NONE);
		stringBuilder = new StringBuilder(200);
	}

	public DebugLogger getLogger () {
		return logger;
	}

	@Override
	public boolean hasReference (String name) {
		return references.containsKey(name);
	}

	@Override
	public ClassReference getReference (String name) {
		if (name == null) return null;
		return references.get(name);
	}

	@Override
	public boolean hasCommand (String name) {
		return commands.containsKey(name);
	}

	public boolean hasCommand (String name, Class<?>... args) {
		return hasCommand((ClassReference<?>)null, name, args);
	}

	public boolean hasCommand (String referenceName, String name, Class<?>... args) {
		return hasCommand(getReference(referenceName), name, args);
	}

	/** Returns the non MethodCommand for the given name
	 * @param name
	 * @return */
	@Override
	public Command getCommand (String name) {
		ObjectSet<Command> allCommands = commands.get(name);

		if (allCommands == null) return null;

		for (Command c : allCommands) {
			if (c instanceof MethodCommand) continue;
			return c;
		}
		return null;
	}

	public boolean hasCommand (ClassReference<?> classReference, String name, Class<?>... args) {
		return getCommand(classReference, name, args) != null;
	}

	public Command getCommand (String commandName, Class<?>... args) {
		return getCommand((ClassReference<?>)null, commandName, args);
	}

	@Override
	public MethodCommand getCommand (String referenceName, String commandName, Class<?>... args) {
		return getCommand(getReference(referenceName), commandName, args);
	}

	public MethodCommand getCommand (ClassReference<?> classReference, String commandName, Class<?>... args) {
		if (commandName == null) return null;

		ObjectSet<Command> allCommands = commands.get(commandName);

		if (allCommands == null) return null;

		if (args == null) args = emptyArgTypes;

		if (classReference == null) {
			for (Command c : allCommands) {
				if (!(c instanceof MethodCommand)) continue;
				MethodCommand mc = (MethodCommand)c;
				if (Utils.areArgsEqual(mc.getArgs(), args, false)) return mc;
			}
		} else {
			for (Command c : allCommands) {
				if (!(c instanceof MethodCommand)) continue;
				MethodCommand mc = (MethodCommand)c;
				if (mc.getClassReference().equals(classReference) && Utils.areArgsEqual(mc.getArgs(), args, false)) return mc;
			}
		}
		return null;
	}

	public ClassReference addReference (Object object) {
		return addReference(null, object);
	}

	public ClassReference addReference (String referenceID, Object object) {
		if (object == null) throw new IllegalArgumentException("Reference object is null");
		if (hasReference(referenceID)) throw new ConsoleRuntimeException("Reference with name already exists");

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
		if (references.containsKey(referenceID)) {
			throw new ConsoleRuntimeException("Reference ID already being used");
		}

		boolean isStatic = object instanceof Class;
		ClassReference reference = null;
		if (isStatic) {
			reference = new StaticReference(referenceID, (Class<?>)object);
		} else {
			reference = new InstanceReference(referenceID, object);
		}

		if (logger.getLevel() >= Logger.INFO) {
			stringBuilder.clear();
			stringBuilder.append("[Add Reference] ");

			if (isStatic) {
				stringBuilder.append("Static");
			} else {
				stringBuilder.append("Instance");
			}

			stringBuilder.append(" Name:").append(reference.getName());

			if (logger.getLevel() == DebugLogger.DEBUG) {
				stringBuilder.append(" Class:").append(reference.getReferenceSimpleName());
				stringBuilder.append(" Full:").append(reference.getReferenceClass().getCanonicalName());
			}

			logger.info(stringBuilder.toString());
		}

		references.put(referenceID, reference);

		return reference;
	}

	@Override
	public void addCommand (String name, Command command) {
		if (name == null || name.isEmpty()) throw new IllegalArgumentException("Name can't be null or empty");
		if (command == null) throw new IllegalArgumentException("Command is null");

		ObjectSet<Command> allCommands = commands.get(name);

		if (allCommands == null) {
			allCommands = new ObjectSet<>();
			commands.put(name, allCommands);
		} else {
			// Only one non MethodCommand can exist for a given name
			for (Command c : allCommands) {
				if (!(c instanceof MethodCommand)) throw new ConsoleRuntimeException("Command already exists");
			}
		}

		allCommands.add(command);
	}

	public void addCommand (Object obj, String methodName, Class<?>... args) {
		ClassReference reference = null;
		if (obj instanceof Class) {
			reference = new StaticReference("", (Class<?>)obj);
		} else {
			reference = new InstanceReference("", obj);
		}
		addCommand(reference, methodName, args);
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
				addReference(name, f.get(classReference.getReference()));
			} catch (ReflectionException e) {
				logger.error(e.getMessage());
			}
		}
	}

	private void addCommandToCache (ClassReference<?> classReference, Method method) {
		if (hasCommand(classReference, method.getName(), method.getParameterTypes())) {
			throw new ConsoleRuntimeException("Identical method already added");
		}

		MethodCommand command = new MethodCommand(classReference, method);

		ObjectSet<Command> allCommands = commands.get(method.getName());
		if (allCommands == null) {
			allCommands = new ObjectSet<>();
			commands.put(method.getName(), allCommands);
		}

		allCommands.add(command);

		if (method.isAnnotationPresent(ConsoleCommand.class)) {
			ConsoleCommand annotation = method.getDeclaredAnnotation(ConsoleCommand.class).getAnnotation(ConsoleCommand.class);
			command.setSuccessMessage(annotation.successMessage());
		}

		if (logger.getLevel() > 0) {
			stringBuilder.clear();
			stringBuilder.append("[Add Command] ");
			stringBuilder.append(command.toString());

			if (logger.getLevel() == DebugLogger.DEBUG) {
				stringBuilder.append(" Class:").append(command.getDeclaringClass().getSimpleName());
				stringBuilder.append(" Full:").append(command.getDeclaringClass().getCanonicalName());
			}

			logger.info(stringBuilder.toString());
		}
	}

	public static void argsToString (StringBuilder builder, Class<?>[] args) {
		if (args.length == 0) {
			builder.append(" ()");
			return;
		}

		builder.append('(');
		for (int i = 0; i < args.length; i++) {
			builder.append(args[i].getSimpleName());
			if (i < (args.length - 1)) builder.append(", ");
		}
		builder.append(')');
	}

}
