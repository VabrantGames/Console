
package com.vabrant.console.executionstrategy;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.github.tommyettinger.ds.ObjectList;
import com.vabrant.console.ConsoleCache;
import com.vabrant.console.ConsoleUtils;
import com.vabrant.console.MethodInfo;
import com.vabrant.console.arguments.*;
import com.vabrant.console.arguments.strategies.simple.*;
import com.vabrant.console.parsers.*;
import com.vabrant.console.parsers.MethodArgumentInfoParser.MethodArgumentInfoParserInput;

import java.util.Arrays;

public class SimpleExecutionStrategy implements ExecutionStrategy {

	private MethodArgumentInfoParserInput methodInfoParserInput;
	private ConsoleCacheAndStringInput cacheAndStringInput;
	private ObjectList<Argument> arguments;
	private ObjectMap<Class<?>, Parsable> parsers;

	public SimpleExecutionStrategy () {
		cacheAndStringInput = new ConsoleCacheAndStringInput();
		methodInfoParserInput = new MethodArgumentInfoParserInput();

		// spotless:off
		arguments = new ObjectList<>(Arrays.asList(
				new IntArgument(new SimpleIntArgumentStrategy()),
				new DoubleArgument(new SimpleDoubleArgumentStrategy()),
				new FloatArgument(new SimpleFloatArgumentStrategy()),
				new LongArgument(new SimpleLongArgumentStrategy()),
				new BooleanArgument(new SimpleBooleanArgumentStrategy()),
				new StringArgument(new SimpleStringArgumentStrategy()),
				new InstanceReferenceArgument(new SimpleInstanceReferenceArgumentStrategy())));
		// spotless:on

		parsers = new ObjectMap<>();
		parsers.put(MethodArgument.class, new MethodArgumentParser());
		parsers.put(DoubleArgument.class, new DoubleArgumentParser());
		parsers.put(IntArgument.class, new IntArgumentParser());
		parsers.put(FloatArgument.class, new FloatArgumentParser());
		parsers.put(LongArgument.class, new LongArgumentParser());
		parsers.put(StringArgument.class, new StringArgumentParser());
		parsers.put(BooleanArgument.class, new BooleanArgumentParser());
		parsers.put(InstanceReferenceArgument.class, new InstanceReferenceParser());
		parsers.put(MethodArgumentInfoParser.class, new MethodArgumentInfoParser());
	}

	@Override
	public Object execute (ExecutionStrategyInput input) throws Exception {
		ConsoleCache cache = input.getConsoleCache();
		String command = input.getText();

		cacheAndStringInput.setConsoleCache(cache);

		Array<String> sections = splitCommand(command);
		MethodArgumentInfo info = (MethodArgumentInfo)parsers.get(MethodArgument.class)
			.parse(cacheAndStringInput.setText(sections.first()));
		Object[] args = null;

		if (sections.size > 1) {
			String[] argsAsStr = new String[sections.size - 1];
			for (int i = 0; i < argsAsStr.length; i++) {
				argsAsStr[i] = sections.get(i + 1);
			}
			args = parseArgs(argsAsStr);
		} else {
			args = ConsoleUtils.EMPTY_ARGUMENTS;
		}

		methodInfoParserInput.setData(info);
		methodInfoParserInput.setArgs(args);

		((MethodInfo)parsers.get(MethodArgumentInfoParser.class).parse(methodInfoParserInput)).invoke(args);

		return null;
	}

	private Object[] parseArgs (String[] argStrs) throws Exception {
		if (argStrs == null) return ConsoleUtils.EMPTY_ARGUMENTS;

		Object[] args = new Object[argStrs.length];

		outer:
		for (int i = 0; i < argStrs.length; i++) {
			String s = argStrs[i];

			for (Argument a : arguments) {
				if (a.isType(s)) {
					Parsable parser = parsers.get(a.getClass());
					args[i] = parser.parse(cacheAndStringInput.setText(s));
					continue outer;
				}
			}

			throw new RuntimeException("[NoArgumentFound] : [Input]:" + s);
		}
		return args;
	}

	private Array<String> splitCommand (String command) {
		final Array<String> strings = new Array<>(String.class);

		int start = 0;
		int len = command.length();
		char[] chars = command.toCharArray();
		boolean lastCharIsSeparator = true;
		boolean insideStringLiteral = false;

		for (int i = 0; i < len; i++) {
			char c = chars[i];

			if (c == '"') {
				insideStringLiteral = !insideStringLiteral;
			}

			if (!insideStringLiteral) {
				if (c == ' ') {
					if (!lastCharIsSeparator) {
						strings.add(command.substring(start, i));
						lastCharIsSeparator = true;
					}
					start = i + 1;
				} else {
					lastCharIsSeparator = false;
				}
			}
		}

		if (start < len) {
			strings.add(command.substring(start));
		}

		return strings;
	}

}
