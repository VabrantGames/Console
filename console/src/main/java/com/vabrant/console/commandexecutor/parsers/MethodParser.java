
package com.vabrant.console.commandexecutor.parsers;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectSet;
import com.vabrant.console.commandexecutor.*;
import com.vabrant.console.ConsoleRuntimeException;
import com.vabrant.console.Utils;

public class MethodParser implements Parsable<ParserContext, Command> {

	private static final Class[] EMPTY_ARGUMENT_TYPES = new Class[0];

	@Override
	public Command parse (ParserContext context) throws Exception {
// return null;
		String text = context.getText();
		CommandCache cache = context.getCache();
		Array<Object> args = context.getArgs();
// ObjectSet<Command> methods = null;
		String referenceName = null;
		String methodName = null;

		// Get methods by name or reference
		if (text.charAt(0) == '.') {
			methodName = text.substring(1);

			if (args.size == 0) {
				Command command = cache.getCommand(methodName);

				if (command == null) {
					throw new ConsoleRuntimeException("No command found for name '" + methodName + "'");
				}

				return command;
			} else {
				ObjectSet<Command> methods = cache.getAllCommandsWithName(methodName);

				if (methods == null) {
					throw new ConsoleRuntimeException("No command for name '" + methodName + "'");
				}

				for (Command c : methods) {
					if (c instanceof MethodCommand) {
						MethodCommand mc = (MethodCommand)c;

						Class[] argTypes = getArgTypes(args);
						if (Utils.areArgsEqual(mc.getArgs(), argTypes, false)) {
							return mc;
						}
					}
				}

				throw new ConsoleRuntimeException(
					"No command found for name '" + methodName + "' with args '" + Utils.argumentsToString(args.items) + "'");
			}

		} else if (Character.isAlphabetic(text.charAt(0))) {
			int sepIdx = text.indexOf('.');
			if (sepIdx != -1) {
				referenceName = text.substring(0, sepIdx);

				ClassReference<?> reference = cache.getReference(referenceName);

				if (reference == null) {
					throw new ConsoleRuntimeException("No reference found for name '" + referenceName + "'");
				}

				methodName = text.substring(sepIdx + 1);

				ObjectSet<MethodCommand> commandsForReference = cache.getAllCommandsForReference(reference);

				if (commandsForReference == null)
					throw new ConsoleRuntimeException("No commands for reference '" + referenceName + "'");

				for (MethodCommand mc : commandsForReference) {
					Class[] argTypes = getArgTypes(args);
					if (mc.getMethodName().equals(methodName) && Utils.areArgsEqual(mc.getArgs(), argTypes, false)) {
						return mc;
					}
				}

				throw new ConsoleRuntimeException(
					"No command found for name '" + methodName + "' with args '" + Utils.argumentsToString(args.items));
			} else {
				methodName = text;

				ObjectSet<Command> methods = cache.getAllCommandsWithName(methodName);

				if (methods == null) {
					throw new ConsoleRuntimeException("No command for name '" + methodName + "'");
				}

				for (Command c : methods) {
					if (c instanceof MethodCommand) {
						MethodCommand mc = (MethodCommand)c;

						Class[] argTypes = getArgTypes(args);
						if (Utils.areArgsEqual(mc.getArgs(), argTypes, false)) {
							return mc;
						}
					}
				}

				throw new ConsoleRuntimeException(
					"No command found for name '" + methodName + "' with args '" + Utils.argumentsToString(args.items));
			}
		}

		throw new ConsoleRuntimeException("Improper use of parser");
	}

	private Class[] getArgTypes (Array<Object> args) {
		if (args.size == 0) return EMPTY_ARGUMENT_TYPES;

		final int argSize = args.size;
		Class[] argTypes = new Class[argSize];

		for (int i = 0, len = argSize, idx = len - 1; i < len; i++, idx--) {
			Object o = args.get(idx);

			if (o instanceof MethodContainer) {
				argTypes[i] = ((MethodContainer)o).getCommand().getReturnType();
			} else {
				argTypes[i] = o.getClass();
			}
		}

		return argTypes;
	}
}
