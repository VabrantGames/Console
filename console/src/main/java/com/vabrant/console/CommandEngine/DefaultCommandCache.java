
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

import java.util.Arrays;

public class DefaultCommandCache implements CommandCache {

	private static final String CONFLICT_NAME_IN_USE_TAG = "[Conflict] (Name already in use)";
	private static final String CONFLICT_REFERENCE_EXISTS_TAG = "[Conflict] (Reference already exists)";
	private static final String NAME_TAG = " [Name]:";
	private static final String CLASS_TAG = " [Class]:";
	private static final String FULL_TAG = " [Full]:";
	private static final String ADDED_TAG = "[Added]";

	// References by name
	private final ObjectMap<String, ClassReference<?>> references;

	// Commands grouped by name
	private final ObjectMap<String, Command> commands;

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

	@Override
	public Command getCommand (String name) {
		return commands.get(name);
	}

	public boolean hasCommand (ClassReference<?> classReference, String name, Class<?>... args) {
		return getCommand(classReference, name, args) != null;
	}

	public Command getCommand (String commandName, Class<?>... args) {
		return getCommand((ClassReference<?>)null, commandName, args);
	}

	public Command getCommand (String referenceName, String commandName, Class<?>... args) {
		return getCommand(getReference(referenceName), commandName, args);
	}

	public Command getCommand (ClassReference<?> classReference, String commandName, Class<?>... args) {
		if (commandName == null) return null;

		Command command = getCommand(commandName);

		if (!(command instanceof MethodCommandManager)) throw new ConsoleRuntimeException("Command is not a MethodCommand");

		ObjectSet<MethodCommand> allCommands = ((MethodCommandManager)command).getCommands();

		if (classReference == null) {
			for (MethodCommand c : allCommands) {
				if (Utils.areArgsEqual(c.getArgs(), args, false)) return c;
			}
		} else {
			for (MethodCommand c : allCommands) {
				if (c.getClassReference().equals(classReference) && Utils.areArgsEqual(c.getArgs(), args, false)) return c;
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

		references.put(referenceID, reference);

		return reference;
	}

	@Override
	public void addCommand (String name, Command command) {
		if (name == null || name.isEmpty()) throw new IllegalArgumentException("Name can't be null or empty");
		if (hasCommand(name)) throw new ConsoleRuntimeException("Command with name already exists");
		commands.put(name, command);
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
		MethodCommandManager manager = null;

		{
			Command command = getCommand(method.getName());

			if (command == null) {
				manager = new MethodCommandManager(method.getName());
				commands.put(method.getName(), manager);
			} else if (!(command instanceof MethodCommandManager)) {
				throw new ConsoleRuntimeException("Non MethodCommand with name already exists.");
			} else {
				manager = (MethodCommandManager)command;
			}
		}

		MethodCommand command = new MethodCommand(classReference, method);

		if (method.isAnnotationPresent(ConsoleCommand.class)) {
			ConsoleCommand annotation = method.getDeclaredAnnotation(ConsoleCommand.class).getAnnotation(ConsoleCommand.class);
			command.setSuccessMessage(annotation.successMessage());
		}

		manager.add(command);

// Get a set containing all added methods for the reference
// ObjectSet<Command> allMethodsForReference = commandsByReference.get(classReference);
// if (allMethodsForReference == null) {
// allMethodsForReference = new ObjectSet<>();
// commandsByReference.put(classReference, allMethodsForReference);
// }
// allMethodsForReference.add(command);

// ObjectSet<Command> methodsWithSameName = this.commands.get(method.getName());
// if (methodsWithSameName == null) {
// methodsWithSameName = new ObjectSet<>();
// commands.put(method.getName(), methodsWithSameName);
// }
// methodsWithSameName.add(command);

// if (logger.getLevel() > 0) {
// stringBuilder.clear();
// stringBuilder.append("[New] [Command] ");
//
// if (logger.getLevel() == DebugLogger.DEBUG) {
// stringBuilder.append("").append(method.getName());
// stringBuilder.append(" Return:").append(command.getReturnType().getCanonicalName());
// stringBuilder.append(" Args:").append(CommandUtils.argsToString(command.getArgs()));
// stringBuilder.append(" Class:").append(command.getDeclaringClass().getSimpleName());
// stringBuilder.append(" Full:").append(command.getDeclaringClass().getCanonicalName());
// } else {
// stringBuilder.append(command.toString());
// }
//
// logger.info(stringBuilder.toString());
// }
	}

	public static class MethodCommandManager implements Command {

		private static StringBuilder STRING_BUILDER = new StringBuilder(200);
		private final static Class[] EMPTY_ARG_TYPES = new Class[0];

		private String name;
		private ObjectSet<MethodCommand> commands;

		public MethodCommandManager (String name) {
			this.name = name;
			commands = new ObjectSet<>();
		}

		public ObjectSet<MethodCommand> getCommands () {
			return commands;
		}

		public void add (MethodCommand command) {
			for (MethodCommand c : commands) {
				if (c.equals(command)) throw new ConsoleRuntimeException("Identical command already added");
			}
			commands.add(command);
		}

		public MethodCommand get (ClassReference reference, Class[] args) {
			if (reference == null) {
				for (MethodCommand c : commands) {
					if (Utils.areArgsEqual(c.getArgs(), args, false)) return c;
				}
			} else {
				for (MethodCommand c : commands) {
					if (c.getClassReference().equals(reference) && Arrays.equals(c.getArgs(), args)) return c;
				}
			}
			return null;
		}

		public boolean containsCommand (MethodCommand command) {
			for (MethodCommand c : commands) {
				if (c.equals(command)) return true;
			}
			return false;
		}

		@Override
		public void setSuccessMessage (String message) {
			throw new ConsoleRuntimeException("Operation not supported");
		}

		@Override
		public String getSuccessMessage () {
			throw new ConsoleRuntimeException("Operation not supported");
		}

		@Override
		public Class getReturnType () {
			throw new ConsoleRuntimeException("Operation not supported");
		}

		@Override
		public Object execute (Object[] args) throws Exception {
			Class[] argTypes = null;

			if (args.length > 0) {
				argTypes = new Class[args.length];

				for (int i = 0; i < args.length; i++) {
					argTypes[i] = args[i].getClass();
				}
			} else {
				argTypes = EMPTY_ARG_TYPES;
			}

			MethodCommand command = get(null, argTypes);

			if (command == null) {
				STRING_BUILDER.clear();
				STRING_BUILDER.append("No command found. Name:");
				STRING_BUILDER.append(name);
				STRING_BUILDER.append(" Args:");
				argsToString(STRING_BUILDER, argTypes);
				throw new ConsoleRuntimeException(STRING_BUILDER.toString());
			}

			return command.execute(args);
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
