package com.vabrant.console.CommandEngine.advanced;

import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectSet;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.Method;
import com.vabrant.console.CommandEngine.*;
import com.vabrant.console.ConsoleRuntimeException;
import com.vabrant.console.Utils;

public class AdvancedCommandCache implements CommandCache {

	private final ObjectMap<String, ClassReference<?>> references;
	private final ObjectMap<ClassReference<?>, ObjectSet<Command>> commandsByReference;
	private final ObjectMap<String, ObjectSet<Command>> commandsByName;
	private final StringBuilder stringBuilder;
	private static final Class[] emptyArgTypes = new Class[0];

	public AdvancedCommandCache () {
		references = new ObjectMap<>();
		commandsByReference = new ObjectMap<>();
		commandsByName = new ObjectMap<>();
		stringBuilder = new StringBuilder(200);
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
	public void addCommand (String name, Command command) {
		if (name == null || name.isEmpty()) throw new IllegalArgumentException("Name can't be null or empty");
		if (command == null) throw new IllegalArgumentException("Command is null");

		ObjectSet<Command> allCommands = commandsByName.get(name);

		if (allCommands == null) {
			allCommands = new ObjectSet<>();
			commandsByName.put(name, allCommands);
		} else {
			for (Command c : allCommands) {
				if (!(c instanceof MethodCommand)) throw new ConsoleRuntimeException("Command already exists");
			}
		}

		allCommands.add(command);
	}

	@Override
	public void addCommand (ClassReference<?> classReference, String name, Class<?>... args) {
		if (classReference == null) throw new IllegalArgumentException("Class reference is null.");
		if (name == null || name.isEmpty()) throw new IllegalArgumentException("Invalid method name");

		boolean isStatic = classReference instanceof StaticReference;

		Method method = null;
		try {
			method = ClassReflection.getMethod(classReference.getReferenceClass(), name, args);

			if (!method.isPublic()) throw new ConsoleRuntimeException("Method must be public");
			if (isStatic && !method.isStatic())
				throw new ConsoleRuntimeException("Static references can only contain static methods");
		} catch (Exception e) {
			throw new ConsoleRuntimeException(e.getMessage(), e);
		}

		addMethodCommandToCache(classReference, method);
	}

	@Override
	public boolean hasCommand (String name) {
		return false;
	}

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
	public MethodCommand getCommand (String referenceName, String methodName, Class<?>... args) {
		return getCommand(getReference(referenceName), methodName, args);
	}

	public MethodCommand getCommand (ClassReference<?> classReference, String commandName, Class<?>... args) {
		if (commandName == null) return null;

		ObjectSet<Command> allCommands = null;

		if (args == null) args = emptyArgTypes;

		if (classReference == null) {
			allCommands = commandsByName.get(commandName);

			if (allCommands == null) return null;

			for (Command c : allCommands) {
				if (!(c instanceof MethodCommand)) continue;
				MethodCommand mc = (MethodCommand)c;
				if (Utils.areArgsEqual(mc.getArgs(), args, false)) return mc;
			}
		} else {
			allCommands = commandsByReference.get(classReference);

			if (allCommands == null) return null;

			for (Command c : allCommands) {
				if (!(c instanceof MethodCommand)) continue;
				MethodCommand mc = (MethodCommand)c;
				if (mc.getClassReference().equals(classReference) && Utils.areArgsEqual(mc.getArgs(), args, false)) return mc;
			}
		}

		return null;
	}

	@Override
	public void addAll (ClassReference<?> classReference) {

	}

	@Override
	public ClassReference addReference (String referenceID, Object object) {
		if (object == null) throw new IllegalArgumentException("Reference object is null");

		if (hasReference(referenceID)) throw new ConsoleRuntimeException("Reference with name already exists.");

		if (referenceID == null || referenceID.isEmpty()) {
			if (object.getClass().isAnnotationPresent(ConsoleReference.class)) {
				ConsoleReference o = ClassReflection.getAnnotation(object.getClass(), ConsoleReference.class)
					.getAnnotation(ConsoleReference.class);
				referenceID = o.value();
			}

			if (referenceID == null || referenceID.isEmpty()) {
				throw new ConsoleRuntimeException("Reference ID can't be null or empty");
			}

			if (hasReference(referenceID)) throw new ConsoleRuntimeException("Reference with name already exists.");
		}

		boolean isStatic = object instanceof Class;
		ClassReference reference = null;
		if (isStatic) {
			reference = new StaticReference(referenceID, (Class<?>)object);
		} else {
			reference = new InstanceReference(referenceID, object);
		}

		// Log

		references.put(referenceID, reference);

		return reference;
	}

	private void addMethodCommandToCache (ClassReference<?> classReference, Method method) {
		if (hasCommand(classReference.getReferenceSimpleName(), method.getName(), method.getParameterTypes())) {
			throw new ConsoleRuntimeException("Identical method already exists");
		}

		MethodCommand command = new MethodCommand(classReference, method);

		ObjectSet<Command> allCommandsForReference = commandsByReference.get(classReference);
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
	}
}
