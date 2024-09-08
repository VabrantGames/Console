
package com.vabrant.console.commandexecutor;

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

import static com.vabrant.console.Utils.STRING_BUILDER;

public class DefaultCommandCache implements CommandCache {

	private boolean includeStaticCommandsForInstanceReferences;
	private final ObjectMap<String, ClassReference<?>> referencesByName;
	private final ObjectMap<String, ObjectSet<Command>> commandsByName;
	private final ObjectMap<ClassReference, ObjectSet<MethodCommand>> commandsByReference;
	private DebugLogger logger;

	public DefaultCommandCache () {
		referencesByName = new ObjectMap<>();
		commandsByName = new ObjectMap<>();
		commandsByReference = new ObjectMap<>();
	}

	public void setIncludeStaticCommandsForInstanceReferences () {
		includeStaticCommandsForInstanceReferences = true;
	}

	public void setLogLevel (int level) {
		if (level == DebugLogger.NONE) return;
		if (logger == null) {
			logger = new DebugLogger(this.getClass(), level);
		} else {
			logger.setLevel(level);
		}
	}

	public DebugLogger getLogger () {
		if (logger == null) {
			logger = new DebugLogger(this.getClass());
		}

		return logger;
	}

	@Override
	public ObjectSet<Command> getAllCommandsWithName (String name) {
		return commandsByName.get(name);
	}

	@Override
	public ObjectSet<MethodCommand> getAllCommandsForReference (ClassReference reference) {
		if (reference == null) throw new IllegalArgumentException("Reference is null");
		return commandsByReference.get(reference);
	}

	@Override
	public boolean hasReference (String name) {
		if (name == null) return false;
		return referencesByName.containsKey(name);
	}

	@Override
	public ClassReference getReference (String name) {
		if (name == null) return null;
		return referencesByName.get(name);
	}

	public ClassReference addReference (String referenceID, Object object) {
		if (object == null) throw new IllegalArgumentException("Reference object is null");
		if (hasReference(referenceID)) throw new ConsoleRuntimeException("Reference with name already exists");

		boolean isStatic = object instanceof Class;

		if (referenceID == null || referenceID.isEmpty()) {
			if (ClassReflection.isAnnotationPresent(isStatic ? (Class)object : object.getClass(), ConsoleReference.class)) {
				ConsoleReference o = ClassReflection
					.getAnnotation(isStatic ? (Class)object : object.getClass(), ConsoleReference.class)
					.getAnnotation(ConsoleReference.class);
				referenceID = o.value();
			}

			if (referenceID == null || referenceID.isEmpty()) {
				throw new ConsoleRuntimeException("Reference ID can't be null or empty");
			}

			if (hasReference(referenceID)) throw new ConsoleRuntimeException("Reference with name already exists");
		}

		ClassReference reference = null;
		if (isStatic) {
			reference = new StaticReference(referenceID, (Class<?>)object);
		} else {
			reference = new InstanceReference(referenceID, object);
		}

		referencesByName.put(referenceID, reference);

		logReference(reference);

		return reference;
	}

	@Override
	public boolean hasCommand (String name) {
		return commandsByName.containsKey(name);
	}

	/** Returns the {@link Command} method for the name, first zero argument {@link MethodCommand} or first {@link MethodCommand}
	 * in that order.
	 *
	 * @param name
	 * @return */
	@Override
	public Command getCommand (String name) {
		ObjectSet<Command> allCommands = commandsByName.get(name);

		if (allCommands == null) return null;

		if (allCommands.size == 1) {
			return allCommands.first();
		}

		boolean noArgCommandFound = false;
		Command command = null;
		for (Command c : allCommands) {
			if (c instanceof MethodCommand) {
				if (noArgCommandFound) continue;

				if (command == null) {
					command = c;
					continue;
				}

				if (((MethodCommand)c).getArgs().length == 0) {
					noArgCommandFound = true;
					command = c;
				}
			} else {
				command = c;
				break;
			}
		}
		return command;
	}

	@Override
	public void addCommand (String name, Command command) {
		if (name == null || name.isEmpty()) throw new IllegalArgumentException("Name can't be null or empty");
		if (command == null) throw new IllegalArgumentException("Command is null");

		ObjectSet<Command> allCommands = commandsByName.get(name);

		if (allCommands == null) {
			allCommands = new ObjectSet<>();
			commandsByName.put(name, allCommands);
		} else {
			// Only one non MethodCommand can exist for a given name
			for (Command c : allCommands) {
				if (!(c instanceof MethodCommand)) throw new ConsoleRuntimeException("Command already exists");
			}
		}

		allCommands.add(command);

		logCommand(name, command);
	}

	public boolean hasMethodCommand (String name, Class<?>... args) {
		return hasMethodCommand((ClassReference<?>)null, name, args);
	}

	@Override
	public boolean hasMethodCommand (String referenceName, String name, Class<?>... args) {
		return hasMethodCommand(getReference(referenceName), name, args);
	}

	public boolean hasMethodCommand (ClassReference<?> classReference, String name, Class<?>... args) {
		return getMethodCommand(classReference, name, args) != null;
	}

	public Command getMethodCommand (String commandName, Class<?>... args) {
		return getMethodCommand((ClassReference<?>)null, commandName, args);
	}

	@Override
	public MethodCommand getMethodCommand (String referenceName, String commandName, Class<?>... args) {
		return getMethodCommand(getReference(referenceName), commandName, args);
	}

	public MethodCommand getMethodCommand (ClassReference<?> classReference, String commandName, Class<?>... args) {
		if (commandName == null) return null;

		if (args == null) args = Utils.EMPTY_ARGUMENT_TYPES;

		if (classReference == null) {
			ObjectSet<Command> allCommands = commandsByName.get(commandName);

			if (allCommands == null) return null;

			for (Command c : allCommands) {
				if (!(c instanceof MethodCommand)) continue;
				MethodCommand mc = (MethodCommand)c;
				if (Utils.areArgsEqual(mc.getArgs(), args, false)) return mc;
			}
		} else {
			ObjectSet<MethodCommand> allCommands = commandsByReference.get(classReference);

			if (allCommands == null) return null;

			for (MethodCommand c : allCommands) {
				if (c.getMethodName().equals(commandName) && Utils.areArgsEqual(c.getArgs(), args, false)) return c;
			}
		}
		return null;
	}

	public void addMethodCommand (String referenceName, Object obj, String methodName, Class<?>... args) {
		addMethodCommand(addReference(referenceName, obj), methodName, args);
	}

	@Override
	public void addMethodCommand (ClassReference<?> classReference, String methodName, Class<?>... args) {
		if (classReference == null) throw new IllegalArgumentException("Class reference is null.");
		if (methodName == null || methodName.isEmpty()) throw new IllegalArgumentException("Invalid method name");

		boolean isStatic = classReference instanceof StaticReference;

		Method method = null;
		try {
			method = ClassReflection.getMethod(classReference.getReferenceClass(), methodName, args);

			if (!method.isPublic()) throw new ConsoleRuntimeException("Method must be public");
			if (!includeStaticCommandsForInstanceReferences) {
				if (isStatic && !method.isStatic()) {
					throw new ConsoleRuntimeException("Non static methods are not allowed for static references");
				} else if (!isStatic && method.isStatic()) {
					throw new ConsoleRuntimeException("Static methods are not allowed for instance references");
				}
			}
		} catch (Exception e) {
			throw new ConsoleRuntimeException(e.getMessage(), e);
		}

		addMethodCommandToCache(classReference, method);
	}

	public void addAll (Object object) {
		addAll(null, object);
	}

	public void addAll (String referenceID, Object object) {
		if (object == null) throw new IllegalArgumentException("Object is null.");

		boolean createCommands = false;
		ClassReference ref = null;

		if (object instanceof ClassReference) {
			ref = (ClassReference)object;
		} else if (ClassReflection.isAnnotationPresent(object instanceof Class ? (Class)object : object.getClass(),
			ConsoleReference.class)) {
			ref = addReference(referenceID, object);
		}

		createCommands = ref != null;

		if (createCommands) {
			Method[] methods = ClassReflection.getMethods(ref.getReferenceClass());
			boolean isStatic = ref instanceof StaticReference;

			for (Method m : methods) {
				if (!m.isPublic()) throw new ConsoleRuntimeException("Method must be public");
				if (!m.isAnnotationPresent(ConsoleCommand.class)
					|| !includeStaticCommandsForInstanceReferences && isStatic && !m.isStatic()
					|| !includeStaticCommandsForInstanceReferences && !isStatic && m.isStatic()) continue;

				addMethodCommandToCache(ref, m);
			}
		}

		// Add fields with the ConsoleReference annotation
		Field[] fields = ClassReflection.getFields(object.getClass());
		for (Field f : fields) {
			if (!f.isPublic() || !f.isAnnotationPresent(ConsoleReference.class)) continue;

			try {
				f.setAccessible(true);
				String name = f.getDeclaredAnnotation(ConsoleReference.class).getAnnotation(ConsoleReference.class).value();
				addReference(name, f.get(object));
			} catch (ReflectionException e) {
				if (logger != null) logger.error(e.getMessage());
			}
		}
	}

	private void addMethodCommandToCache (ClassReference<?> classReference, Method method) {
		if (hasMethodCommand(classReference, method.getName(), method.getParameterTypes())) {
			throw new ConsoleRuntimeException("Identical method already added");
		}

		MethodCommand command = new MethodCommand(classReference, method);

		ObjectSet<MethodCommand> allCommandsForReference = commandsByReference.get(classReference);
		if (allCommandsForReference == null) {
			allCommandsForReference = new ObjectSet<>();
			commandsByReference.put(classReference, allCommandsForReference);
		}
		allCommandsForReference.add(command);

		ObjectSet<Command> allCommandsForName = commandsByName.get(method.getName());
		if (allCommandsForName == null) {
			allCommandsForName = new ObjectSet<>();
			commandsByName.put(method.getName(), allCommandsForName);
		}
		allCommandsForName.add(command);

		if (method.isAnnotationPresent(ConsoleCommand.class)) {
			ConsoleCommand annotation = method.getDeclaredAnnotation(ConsoleCommand.class).getAnnotation(ConsoleCommand.class);
			command.setSuccessMessage(annotation.successMessage());
		}

		logMethodCommand(command);
	}

	private void argsToString (StringBuilder builder, Class<?>[] args) {
		if (args.length == 0) {
			builder.append("()");
			return;
		}

		builder.append('(');
		for (int i = 0; i < args.length; i++) {
			builder.append(args[i].getSimpleName());
			if (i < (args.length - 1)) builder.append(", ");
		}
		builder.append(')');
	}

	private void logCommand (String name, Command command) {
		if (logger == null || logger.getLevel() == DebugLogger.NONE) return;

		STRING_BUILDER.clear();
		STRING_BUILDER.append("Name=");
		STRING_BUILDER.append(name);
		STRING_BUILDER.append(" Return=");
		STRING_BUILDER.append(command.getReturnType());

		logger.info("Add Command", STRING_BUILDER.toString());
	}

	private void logMethodCommand (MethodCommand command) {
		if (logger == null || logger.getLevel() == DebugLogger.NONE) return;

		STRING_BUILDER.clear();
		STRING_BUILDER.append("Name=");
		STRING_BUILDER.append(command.getMethodName());
		STRING_BUILDER.append(" Return=");
		STRING_BUILDER.append(command.getReturnType());
		STRING_BUILDER.append(" Args=");
		argsToString(STRING_BUILDER, command.getArgs());
		STRING_BUILDER.append(" Class=");
		STRING_BUILDER.append(command.getDeclaringClass().getSimpleName());

		if (logger.getLevel() == DebugLogger.DEBUG) {
			STRING_BUILDER.append(" Full=");
			STRING_BUILDER.append(command.getDeclaringClass().getCanonicalName());
		}

		logger.info("Add Command", STRING_BUILDER.toString());
	}

	private void logReference (ClassReference reference) {
		if (logger == null || logger.getLevel() == DebugLogger.NONE) return;

		boolean isStatic = reference instanceof StaticReference;

		STRING_BUILDER.clear();
		if (isStatic) {
			STRING_BUILDER.append("Static");
		} else {
			STRING_BUILDER.append("Instance");
		}

		STRING_BUILDER.append(" Name=");
		STRING_BUILDER.append(reference.getName());
		STRING_BUILDER.append(" Class=");
		STRING_BUILDER.append(reference.getReferenceClass().getSimpleName());
		STRING_BUILDER.append(" Full=");
		STRING_BUILDER.append(reference.getReferenceClass().getCanonicalName());

		logger.info("Add Reference", STRING_BUILDER.toString());
	}

}
