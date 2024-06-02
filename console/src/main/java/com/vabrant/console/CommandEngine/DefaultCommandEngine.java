
package com.vabrant.console.CommandEngine;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.github.tommyettinger.ds.ObjectList;
import com.vabrant.console.CommandEngine.arguments.*;
import com.vabrant.console.CommandEngine.parsers.*;
import com.vabrant.console.ConsoleRuntimeException;
import com.vabrant.console.DebugLogger;
import com.vabrant.console.log.LogManager;

public class DefaultCommandEngine extends AbstractCommandEngine<CommandCache> {

// private CommandCache globalCache;
// private DebugLogger logger;
// private ObjectList<Argument> arguments;
// private ObjectMap<Class, Parsable> parsers;
// private ParserContext parserContext;
	private Object[] emptyArgs = new Object[0];

	public DefaultCommandEngine (LogManager logManager) {
		this(logManager, null);
	}

	public DefaultCommandEngine (LogManager logManager, CommandCache globalCache) {
		super(logManager, globalCache);
		logger = new DebugLogger(this.getClass());
		arguments = new ObjectList<>();
		parsers = new ObjectMap<>();
		parserContext = new DefaultParserContext(globalCache);

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
	public CommandEngineExecutionResult execute (CommandCache cache, Object o) {
		executionResult.clear();

		Object[] args = null;
		Command command = null;
		String commandAsString = null;

		parserContext.setCache(cache);

		try {
			if (o instanceof String) {
				commandAsString = (String)o;

				Array<String> splitCmd = splitCommand((String)o);

				if (splitCmd.size > 1) {
					args = new Object[splitCmd.size - 1];
					Class[] argTypes = new Class[splitCmd.size - 1];

					for (int i = 1; i < splitCmd.size; i++) {
						parserContext.setText(splitCmd.get(i));
						for (Argument a : arguments) {
							if (a.isType(splitCmd.get(i))) {
								args[i - 1] = parsers.get(a.getClass()).parse(parserContext);
								break;
							}
						}
						argTypes[i - 1] = args[i - 1].getClass();
					}

					command = cache.getCommand(null, splitCmd.get(0), argTypes);
				} else {
					String cmd = splitCmd.get(0);

					if (cmd.contains(".")) {
						int idx = cmd.indexOf('.');
						command = cache.getCommand(cmd.substring(idx), cmd.substring(idx + 1));
					} else {
						command = cache.getCommand(splitCmd.get(0));
						if (command == null) command = cache.getCommand(null, splitCmd.get(0), null);
					}

				}

				if (command == null) throw new ConsoleRuntimeException("No command found: " + splitCmd.first());
			} else if (o instanceof Object[]) {
				Object[] input = (Object[])o;

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

					command = cache.getCommand(null, (String)input[0], argTypes);
				} else {
					command = cache.getCommand((String)input[0]);
				}

				if (command == null) throw new ConsoleRuntimeException("No command found: " + (String)input[0]);

				if (input.length > 1) {
					args = new Object[input.length - 1];

					System.arraycopy(input, 1, args, 0, args.length);
				}
			}

			if (args == null) {
				args = emptyArgs;
			}

// result = command.execute(args);
			System.out.println("Working");
			executionResult.setResult(command.execute(args));
			executionResult.setExecutionStatus(true);
			printCommandToLogManager(commandAsString);
			printSuccessMessageToLogManager(command.getSuccessMessage());
			System.out.println(command instanceof MethodCommand);
		} catch (Exception e) {
			e.printStackTrace();
			executionResult.setErrorString(e);
		}

		return executionResult;
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

}
