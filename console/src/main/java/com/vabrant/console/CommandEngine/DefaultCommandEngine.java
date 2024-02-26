package com.vabrant.console.CommandEngine;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.github.tommyettinger.ds.ObjectList;
import com.vabrant.console.CommandEngine.arguments.*;
import com.vabrant.console.CommandEngine.parsers.*;
import com.vabrant.console.ConsoleRuntimeException;
import com.vabrant.console.DebugLogger;

public class DefaultCommandEngine implements CommandEngine<CommandCache> {

	private CommandCache globalCache;
	private DebugLogger logger;
	private ObjectList<Argument> arguments;
	private ObjectMap<Class, Parsable> parsers;
	private ParserContext parserContext;
	private Object[] emptyArgs = new Object[0];

	public DefaultCommandEngine () {
		this(null);
	}

	public DefaultCommandEngine (CommandCache globalCache) {
		logger = new DebugLogger(this.getClass());
		arguments = new ObjectList<>();
		parsers = new ObjectMap<>();
		parserContext = new DefaultParserContext(globalCache);

		arguments.addAll(new Argument[]{
			new IntArgument(),
			new DoubleArgument(),
			new FloatArgument(),
			new LongArgument(),
			new BooleanArgument(),
			new StringArgument(),
			new GlobalClassReferenceArgument(),
			new ClassReferenceArgument(),
		});

		parsers.put(DoubleArgument.class, new DoubleArgumentParser());
		parsers.put(IntArgument.class, new IntArgumentParser());
		parsers.put(FloatArgument.class, new FloatArgumentParser());
		parsers.put(LongArgument.class, new LongArgumentParser());
		parsers.put(StringArgument.class, new StringArgumentParser());
		parsers.put(BooleanArgument.class, new BooleanArgumentParser());
		parsers.put(GlobalClassReferenceArgument.class, new GlobalClassReferenceParser());
		parsers.put(ClassReferenceArgument.class, new ClassReferenceParser());
	}

	public ParserContext getParserContext() {
		return parserContext;
	}

	public DebugLogger getLogger() {
		return logger;
	}

	@Override
	public Object execute (CommandCache cache, Object o) throws Exception {
		Object[] args = null;
		Command command = null;

		parserContext.setCache(cache);

		if (o instanceof String) {
			Array<String> splitCmd = splitCommand((String) o);
			command = cache.getCommand(splitCmd.get(0));

			if (command == null) throw new ConsoleRuntimeException("No command found: " + splitCmd.first());

			if (splitCmd.size > 1) {
				args = new Object[splitCmd.size - 1];
				for (int i = 1; i < splitCmd.size; i++) {
					parserContext.setText(splitCmd.get(i));
					for (Argument a : arguments) {
						if (a.isType(splitCmd.get(i))) {
							args[i - 1] = parsers.get(a.getClass()).parse(parserContext);
							break;
						}
					}
				}
			}
		} else if (o instanceof Object[]) {
			Object[] input = (Object[]) o;

			if (!(input[0] instanceof String)) throw new ConsoleRuntimeException("First argument must be a string");

			command = cache.getCommand((String) input[0]);

			if (command == null) throw new ConsoleRuntimeException("No command found: " + (String) input[0]);

			if (input.length > 1) {
				args = new Object[input.length - 1];

				System.arraycopy(input, 1, args, 0, args.length);
			}
		}

		if (args == null) {
			args = emptyArgs;
		}

		return command.execute(args);
	}

	private Array<String> splitCommand(String command) {
		command = command.trim();
		int start = 0;
		Array<String> strings = new Array<>();
		int len = command.length();
		char[] chars = command.toCharArray();
		boolean insideStringLiteral = false;

		for (int i = 0; i < len; i++) {
			boolean skip = false;
			char c  = chars[i];

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
