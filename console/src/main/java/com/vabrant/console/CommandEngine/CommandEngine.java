package com.vabrant.console.CommandEngine;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.Method;
import com.vabrant.console.Console;
import com.vabrant.console.ConsoleExtension;
import com.vabrant.console.ConsoleRuntimeException;
import com.vabrant.console.DebugLogger;

public interface CommandEngine<T extends CommandCache> {
	Object execute (T cache, Object o) throws Exception;

//	private ObjectMap<String, ClassReference<?>> classReferences;
//	private ObjectMap<String, Command> commands;
//	private ObjectMap<Class, CommandParseStrategy> strategies;
//	private DebugLogger logger;
//	private Console console;
//	private ConsoleExtension extension;

//	public CommandEngine() {
//		classReferences = new ObjectMap<>();
//		commands = new ObjectMap<>();
//		strategies = new ObjectMap<>();
//		logger = new DebugLogger(this.getClass());
//
//		strategies.put(MethodCommand.class, new MethodCommandParseStrategy());
//	}
//
//	public DebugLogger getLogger() {
//		return logger;
//	}
//
//	public void addReference (ClassReference<?> reference) {
//		addReference(null, reference);
//	}
//
//	public void addReference (String ID, ClassReference<?> reference) {
//
//	}
//
//	public void addReference (Object o) {
//
//	}
//
//	public ClassReference addReference (String ID, Object o) {
//		boolean isStatic = o instanceof Class;
//		ClassReference<?> reference = getReference(o);
//
//		if (reference != null) {
//			throw new ConsoleRuntimeException("Reference with object already exists");
//		}
//
//		if (ID == null || ID.isEmpty()) {
//			if (o.getClass().isAnnotationPresent(ConsoleReference.class)) {
//				ConsoleReference cr = ClassReflection.getAnnotation(o.getClass(), ConsoleReference.class).getAnnotation(ConsoleReference.class);
//				ID = cr.value();
//			}
//
//			if (ID == null || ID.isEmpty()) {
//				throw new ConsoleRuntimeException("Reference ID can't be empty or null");
//			}
//		}
//
//		if (classReferences.containsKey(ID)) {
//			throw new ConsoleRuntimeException("Reference with ID already exists");
//		}
//
//		if (isStatic) {
//			throw new ConsoleRuntimeException("Not implemented yet");
//		} else {
//			reference = new InstanceReference(ID, o);
//		}
//
//		return reference;
//	}
//
//	public ClassReference getReference (Object o) {
//		return null;
//	}
//
//	public ClassReference getReference(String name) {
//		return classReferences.get(name);
//	}
//
//	public void addCommand (Object obj, String methodName, Class<?>... args) {
//		addCommand(new InstanceReference(null, obj), methodName, args);
//	}
//
//	public void addCommand (ClassReference<?> reference, String name, Class<?>... args) {
//		if (commands.containsKey(name)) throw new ConsoleRuntimeException("Command with name already added");
//
//		try {
//			Method m = ClassReflection.getMethod(reference.getReferenceClass(), name, args);
//			if (!m.isPublic()) throw new ConsoleRuntimeException("Method must be public");
//			commands.put(name, new MethodCommand(reference, m));
//			logger.info("Command Added", name);
//		} catch (Exception e) {
//			throw new ConsoleRuntimeException(e);
//		}
//	}
//
//	public Boolean execute (Object o) throws Exception {
//		Object returnValue = null;
//		Object[] args = null;
//		Command command = null;
//
//		if (o instanceof String) {
//			Array<String> splitCmd = splitCommand((String) o);
////			String method = splitCmd.get(0);
//			command = commands.get(splitCmd.get(0));
//
//			// No command found
//			if (command == null) throw new ConsoleRuntimeException("No command found");
//
////			Object[] args = null;
//			if (splitCmd.size > 1) {
//				CommandParseStrategy strat = strategies.get(command.getClass());
//				args = strat.parse(splitCmd);
//			}
//
////			if (splitCmd.size > 1) {
////				args = parseArguments(command, splitCmd);
////			}
////
////			returnValue = command.execute(args);
//
////			return true;
//		} else if (o instanceof Object[]) {
//			Object[] input = (Object[]) o;
//
//			if (!(input[0] instanceof String)) throw new ConsoleRuntimeException("First argument must be a string");
//
//			command = commands.get((String) input[0]);
//
//			if (command == null) throw new ConsoleRuntimeException("No command found");
//
//			if (input.length > 1) {
//				args = new Object[input.length - 1];
//
//				for (int i = 0; i < args.length; i++) {
//					if (args[i] instanceof String && ((String) args[i]).startsWith("$")) {
//						args[i] =
//					} else {
//						args[i] = input[i + 1]	;
//					}
////					args[i] =
//				}
//
////				final int argLength = input.length - 1;
////				args = new Object[argLength];
////				System.arraycopy(input, 1, args, 0, argLength);
//			}
////
////			command.execute(args);
////
////			return true;
//		}
//
////		if (cmd == null || cmd.length == 0 || !(cmd[0] instanceof String)) return false;
////
////		Command command = commands.get((String) cmd[0]);
////
////		// No command found
////		if (command == null) throw new ConsoleRuntimeException("");
////
////		// Incorrect arg length
////		if (command.getArgs().length != (cmd.length - 1));
//
//		return false;
//	}
//
//	private Array<String> splitCommand(String command) {
//		command = command.trim();
//		int start = 0;
//		Array<String> strings = new Array<>();
//		int len = command.length();
//		char[] chars = command.toCharArray();
//		boolean insideStringLiteral = false;
//
//		for (int i = 0; i < len; i++) {
//			boolean skip = false;
//			char c  = chars[i];
//
//			if (c == '"') {
//				insideStringLiteral = !insideStringLiteral;
//			}
//
//			if (insideStringLiteral) continue;
//
//			if (c == ' ') {
//				if (start == i) {
//					start++;
//					continue;
//				}
//
//				strings.add(command.substring(start, i));
//				start = i + 1;
//			}
//		}
//
//		if (start < len) {
//			strings.add(command.substring(start));
//		}
//
//		return strings;
//	}
//
//	private Object[] parseArguments (MethodCommand command, Array<String> splitCmd) {
//		// Don't count the command name. arg[0]
//		int argLength = splitCmd.size - 1;
//
//		// Arg length doesn't match command arg length
//		if (command.getArgs().length != argLength) throw new ConsoleRuntimeException("");
//
//		Class[] cmdClasses = command.getArgs();
//		Object[] args = new Object[argLength];
//
//		for (int i = 0; i < argLength; i++) {
//			Class c = cmdClasses[i];
//			String cmd = splitCmd.get(i + 1);
//
////			if (cmd.startsWith("$")) {
////				continue;
////			}
//
//			if (c.equals(int.class) || c.equals(Integer.class)) {
//				args[i] = Integer.parseInt(cmd);
//			} else if (c.equals(float.class) || c.equals(Float.class)) {
//				args[i] = Float.parseFloat(cmd);
//			} else if (c.equals(double.class)|| c.equals(Double.class)) {
//				args[i] = Double.parseDouble(cmd);
//			} else if (c.equals(long.class) || c.equals(Long.class)) {
//				args[i] = Long.parseLong(cmd);
//			} else if (c.equals(boolean.class) || c.equals(Boolean.class)) {
//				args[i] = Boolean.parseBoolean(cmd);
//			} else if (c.equals(String.class)) {
//				args[i] = cmd;
//			} else {
//				if (cmd.startsWith("$")) {
//
//				}
//
////				throw new ConsoleRuntimeException("Error parsing argument: " + cmd);
//			}
//		}
//
//		return args;
//	}
//
//	interface CommandParseStrategy {
//		Object[] parse (Array<String> commandSections);
//	}
//
//	static class MethodCommandParseStrategy implements CommandParseStrategy {
//
//		@Override
//		public Object[] parse (Array<String> commandSections) {
//			return new Object[0];
//		}
//	}
}
