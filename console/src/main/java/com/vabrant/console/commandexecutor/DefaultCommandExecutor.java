
package com.vabrant.console.commandexecutor;

import com.badlogic.gdx.utils.Array;
import com.vabrant.console.Utils;
import com.vabrant.console.commandexecutor.arguments.*;
import com.vabrant.console.commandexecutor.parsers.*;
import com.vabrant.console.ConsoleRuntimeException;
import com.vabrant.console.DebugLogger;
import com.vabrant.console.log.LogManager;

/** TODO: Finish documentation The default CommandExecutor that splits commands by spaces.
 *
 * Command format: command arg1 arg2... reference.command arg1 arg2... */
public class DefaultCommandExecutor extends AbstractCommandExecutor<CommandCache> {

	private Command command;

	public DefaultCommandExecutor (LogManager logManager) {
		this(logManager, null);
	}

	public DefaultCommandExecutor (LogManager logManager, CommandCache globalCache) {
		super(logManager, globalCache);

		arguments.addAll(new Argument[] {new IntArgument(), new DoubleArgument(), new FloatArgument(), new LongArgument(),
			new BooleanArgument(), new StringArgument(), new GlobalClassReferenceArgument(), new ClassReferenceArgument(),});

		parsers.put(DoubleArgument.class, new DoubleArgumentParser());
		parsers.put(IntArgument.class, new IntArgumentParser());
		parsers.put(FloatArgument.class, new FloatArgumentParser());
		parsers.put(LongArgument.class, new LongArgumentParser());
		parsers.put(StringArgument.class, new StringArgumentParser());
		parsers.put(BooleanArgument.class, new BooleanArgumentParser());
		parsers.put(GlobalClassReferenceArgument.class, new GlobalClassReferenceParser());
		parsers.put(ClassReferenceArgument.class, new ClassReferenceParser());
	}

	public ParserContext getParserContext () {
		return parserContext;
	}

	public DebugLogger getLogger () {
		return logger;
	}

	@Override
	public CommandExecutorResult execute (CommandCache cache, Object o) {
		executionResult.clear();
		command = null;
		Object[] args = null;
		String commandAsString = null;
		parserContext.setCache(cache);

		try {
			if (o instanceof String) {
				commandAsString = (String)o;

				Array<String> splitCmd = splitCommand((String)o);

				if (splitCmd.size > 1) {
					args = executeStringCommand(cache, splitCmd);
				} else {
					executeZeroArgumentStringCommand(cache, splitCmd.get(0));
				}
			} else if (o instanceof Object[]) {
				args = executeArrayCommand(cache, (Object[])o);
			} else {
				throw new ConsoleRuntimeException("Operation not supported");
			}

			if (args == null) {
				args = Utils.EMPTY_ARGUMENTS;
			}

			executionResult.setResult(command.execute(args));
			executionResult.setExecutionStatus(true);
			printCommandToLogManager(commandAsString);
			printSuccessMessageToLogManager(command.getSuccessMessage());
		} catch (Exception e) {
			e.printStackTrace();
			executionResult.setErrorString(e);
		}

		return executionResult;
	}

	private Object[] executeStringCommand (CommandCache cache, Array<String> splitCmd) throws Exception {
		Object[] args = new Object[splitCmd.size - 1];
		Class[] argTypes = new Class[args.length];

		for (int i = 1; i < splitCmd.size; i++) {
			parserContext.setText(splitCmd.get(i));
			Object arg = null;

			for (Argument a : arguments) {
				if (a.isType(splitCmd.get(i))) {
					arg = parsers.get(a.getClass()).parse(parserContext);
					break;
				}
			}

			if (arg == null) throw new ConsoleRuntimeException("No argument found");
			args[i - 1] = arg;
			argTypes[i - 1] = arg.getClass();
		}

		command = cache.getMethodCommand(null, splitCmd.get(0), argTypes);

		if (command == null) throw new ConsoleRuntimeException(getMethodErrorString(splitCmd.first(), argTypes));

		return args;
	}

	private void executeZeroArgumentStringCommand (CommandCache cache, String cmd) throws Exception {
		if (cmd.contains(".")) {
			int idx = cmd.indexOf('.');
			command = cache.getMethodCommand(cmd.substring(idx), cmd.substring(idx + 1));
		} else {
			command = cache.getCommand(cmd);
			if (command == null) command = cache.getMethodCommand(null, cmd, null);
		}

		if (command == null) throw new ConsoleRuntimeException(getMethodErrorString(cmd, null));
	}

	private Object[] executeArrayCommand (CommandCache cache, Object[] input) {
		Object[] args = null;

		if (!(input[0] instanceof String)) {
			throw new ConsoleRuntimeException("First argument must be a string");
		}

		int argLength = input.length - 1;
		Class[] argTypes;

		if (argLength > 0) {
			argTypes = new Class[argLength];

			for (int i = 1; i < input.length; i++) {
				argTypes[i - 1] = input[i].getClass();
			}

			command = cache.getMethodCommand(null, (String)input[0], argTypes);
		} else {
			command = cache.getCommand((String)input[0]);
		}

		if (command == null) throw new ConsoleRuntimeException("No command found: " + (String)input[0]);

		if (input.length > 1) {
			args = new Object[input.length - 1];

			System.arraycopy(input, 1, args, 0, args.length);
		}

		return args;
	}

	private Array<String> splitCommand (String command) {
		command = command.trim();
		int start = 0;
		Array<String> strings = new Array<>();
		int len = command.length();
		char[] chars = command.toCharArray();
		boolean insideStringLiteral = false;

		for (int i = 0; i < len; i++) {
			boolean skip = false;
			char c = chars[i];

			if (c == '"') {
				insideStringLiteral = !insideStringLiteral;
			}

			if (insideStringLiteral) continue;

			if (c == ' ') {
				if (start == i) {
					start++;
					continue;
				}

				strings.add(command.substring(start, i));
				start = i + 1;
			}
		}

		if (start < len) {
			strings.add(command.substring(start));
		}

		return strings;
	}

	private String getMethodErrorString (String commandName, Class[] argTypes) {
		StringBuilder builder = new StringBuilder(50);

		builder.append("No command found: ");
		builder.append(commandName);
		builder.append(" (");

		if (argTypes != null) {
			for (int i = 0; i < argTypes.length; i++) {
				builder.append(argTypes[i].getSimpleName());

				if (i != argTypes.length - 1) {
					builder.append(',');
				}
			}
		}

		builder.append(')');

		return builder.toString();
	}

}
