
package com.vabrant.console.executionstrategy;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.Pools;
import com.badlogic.gdx.utils.Queue;
import com.github.tommyettinger.ds.ObjectList;
import com.vabrant.console.ConsoleCache;
import com.vabrant.console.ExecutionStrategy;
import com.vabrant.console.MethodInfo;
import com.vabrant.console.arguments.*;
import com.vabrant.console.exceptions.InvalidFormatException;
import com.vabrant.console.log.LogLevel;
import com.vabrant.console.parsers.*;

import java.util.Arrays;

public class CommandExecutionStrategy extends ExecutionStrategy<String, CommandExecutionEvent> {

	public static final String COMMAND_FAIL_EVENT = "fail";
	public static final String COMMAND_SUCCESS_EVENT = "success";

	private final boolean debug = false;

	private ParserContext parserContext;
	private ObjectList<Argument> arguments;
	private ObjectMap<Class<?>, Parsable> parsers;

	private ConsoleCache cache;
	private CommandExecutionEvent event;

	public CommandExecutionStrategy () {
		this(false);
	}

	public CommandExecutionStrategy (boolean debug) {
// this.debug = debug;

		eventManager.addEvent(COMMAND_FAIL_EVENT);
		eventManager.addEvent(COMMAND_SUCCESS_EVENT);
		parserContext = new ParserContext();
		event = new CommandExecutionEvent();

		// spotless:off
		arguments = new ObjectList<>(Arrays.asList(
				new IntArgument(),
				new DoubleArgument(),
				new FloatArgument(),
				new LongArgument(),
				new BooleanArgument(),
				new StringArgument(),
				new InstanceReferenceArgument(),
				new MethodArgument()
		));
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
		parsers.put(MethodParser.class, new MethodParser());
	}

	public void setConsoleCache (ConsoleCache cache) {
		this.cache = cache;
		parserContext.setConsoleCache(cache);
	}

	public ConsoleCache getConsoleCache () {
		return cache;
	}

	@Override
	public Boolean execute (String command) {
		boolean executionStatus = true;
		Array<MethodContainer> containers = null;

		try {
			if (cache == null) {
				throw new RuntimeException("No cache set");
			}

			if (debug) {
				System.out.println("========== Command ==========");
				System.out.println(command);
			}

			Array<String> sections = createSections(command);
			containers = createAndParseContainersAndArguments(cache, sections);

			for (int i = containers.size - 1; i >= 0; i--) {
				containers.get(i).execute(null);
			}

			event.clear();
			event.setCommand(command);
			eventManager.fire(COMMAND_SUCCESS_EVENT, null);
		} catch (Exception e) {
			log(e.getMessage(), LogLevel.ERROR);

			event.clear();
			event.setCommand(command);
			event.setErrorMessage(e.getMessage());
			eventManager.fire(COMMAND_FAIL_EVENT, event);

			executionStatus = false;
		}

		if (containers != null) Pools.freeAll(containers, true);
		parserContext.clear();

		return executionStatus;
	}

	private Array<String> createSections (String command) {
		Array<String> strings = new Array<>();

		int start = 0;
		int len = command.length();
		int containersAmount = 0;
		char[] chars = command.toCharArray();
		boolean insideStringLiteral = false;
		char lastChar = 0x00;

		if (!Character.isAlphabetic(chars[0])) {
			throw new InvalidFormatException("Command must start with a letter a-zA-z");
		}

		for (int i = 0; i < len; i++) {
			boolean skip = false;
			char c = chars[i];

			if (c == '"') {
				insideStringLiteral = !insideStringLiteral;
			}

			if (insideStringLiteral) continue;

			switch (c) {
			case ' ':
				if (start == i) {
					start++;
					continue;
				}

				strings.add(command.substring(start, i));
				start = i + 1;
				break;
			case ',':
				// Check for "method , arg". Correct format: "method(arg , arg)"
				if (containersAmount == 0) {
					throw new InvalidFormatException("',' can only be used inside ()");
				}

				if (start == i) {
					// Check for double ",," in command
					if (lastChar == ',') {
						throw new InvalidFormatException("Invalid use of ',' (Command) " + command.substring(0, i + 1));
					} else {
						start++;
						break;
					}
				}
				strings.add(command.substring(start, i));
				start = i + 1;
				break;
			case '(':
				// Check of ",(". No method specified. Correct format ", method("
				if (lastChar == ',') {
					throw new InvalidFormatException("Invalid use of ',' (Command) " + command.substring(0, i + 1));
				}

				skip = true;
				containersAmount++;
			case ')':
				if (lastChar == '(') {
					throw new InvalidFormatException("'()' can't be empty");
				}

				if (!skip) containersAmount--;

				// Check for too many ')
				if (containersAmount < 0) {
					throw new InvalidFormatException("Unnecessary ')'");
				}

				if (start < i) {
					strings.add(command.substring(start, i));
				}
				strings.add(command.substring(i, i + 1));
				start = i + 1;
				break;
			}

			lastChar = c;
		}

		// Check for unclosed parenthesis
		if (containersAmount > 0) {
			throw new InvalidFormatException("Missing ')'");
		}

		if (start < len) {
			strings.add(command.substring(start));
		}

		if (debug) {
			StringBuilder builder = new StringBuilder();
			builder.append("========== CreateSections ==========\n");
			builder.append("[");
			for (int i = 0; i < strings.size; i++) {
				builder.append(strings.get(i));

				if (i < (strings.size - 1)) {
					builder.append(", ");
				}
			}
			builder.append("]");
			System.out.println(builder);
		}

		return strings;
	}

	private Array<MethodContainer> createAndParseContainersAndArguments (ConsoleCache cache, Array<String> sections)
		throws Exception {
		boolean rootParsed = false;
		Array<MethodContainer> containers = new Array<>();
		MethodContainer rootContainer = null;
		MethodContainer currentContainer = null;
		Queue<MethodContainer> previousContainerStack = new Queue<>();

		for (int i = sections.size - 1; i >= 1; i--) {
			String section = sections.get(i);
			String nextSection = sections.get(i - 1);

			if (section.startsWith(")")) {
				MethodContainer c = Pools.obtain(MethodContainer.class);
				previousContainerStack.addLast(c);
				containers.add(c);
				currentContainer = c;
			} else if (section.startsWith("(")) {
				parseContainer(nextSection, currentContainer);

				if (i == 1) {
					rootParsed = true;
				} else {
					if (currentContainer.getMethodInfo().getMethodReference().getReturnType().equals(void.class)) {
						throw new RuntimeException(
							"Method has no return type '" + currentContainer.getMethodInfo().getMethodReference().toString() + "'");
					}

					MethodContainer temp = currentContainer;
					previousContainerStack.removeLast();

					// Root container is only created when needed
					// For commands like print(5) the root container will not be used since ')' already creates a
					// container.
					// For more complex commands like print 'add(5 , 5)' , the root command will be created when the
					// add container closes.
					if (previousContainerStack.size == 0) {
						if (rootContainer == null) {
							rootContainer = Pools.obtain(MethodContainer.class);
						}
						currentContainer = rootContainer;
					} else {
						currentContainer = previousContainerStack.last();
					}

					currentContainer.addArgument(temp);
				}

				i--;
			} else {
				if (currentContainer == null) {
					rootContainer = new MethodContainer();
					currentContainer = rootContainer;
				}

				parseArgument(section, currentContainer, containers);
			}
		}

		if (!rootParsed) {
			if (rootContainer == null) {
				rootContainer = Pools.obtain(MethodContainer.class);
			}

			parseContainer(sections.first(), rootContainer);
			containers.insert(0, rootContainer);
		}

		if (debug) {
			StringBuilder builder = new StringBuilder();
			builder.append("========== Containers =========");
			for (int i = 0; i < containers.size; i++) {
				MethodContainer c = containers.get(i);
				builder.append(System.lineSeparator());
				builder.append(c.getMethodInfo().getMethodReference().getName());
				builder.append("(");

				for (int j = c.getArguments().size - 1; j >= 0; j--) {
					Object o = c.getArguments().get(j);

					if (o instanceof MethodContainer) {
						builder.append("Container");
					} else {
						builder.append(o);
					}

					if (j > 0) {
						builder.append(" , ");
					}
				}
				builder.append(')');
			}
			System.out.println(builder);
		}

		return containers;
	}

	private void parseContainer (String section, MethodContainer container) throws Exception {
		parserContext.setText(section);
		parserContext.setArgs(container.getArguments());
		container.setMethodInfo((MethodInfo)parsers.get(MethodParser.class).parse(parserContext));
		parserContext.setArgs(null);
	}

	private void parseArgument (String str, MethodContainer container, Array<MethodContainer> containers) throws Exception {
		Object parsed = null;
		for (Argument a : arguments) {
			if (a.isType(str)) {
				if (a instanceof MethodArgument) {
					MethodContainer c = Pools.obtain(MethodContainer.class);
					parseContainer(str, c);

					if (c.getMethodInfo().getMethodReference().getReturnType().equals(void.class)) {
						throw new RuntimeException(
							"Method has no return type '" + c.getMethodInfo().getMethodReference().toString() + "'");
					}

					parsed = c;
					containers.add(c);
				} else {
					parsed = parsers.get(a.getClass()).parse(parserContext.setText(str));
				}

				container.addArgument(parsed);
				break;
			}
		}
		if (parsed == null) throw new RuntimeException("No argument type found for '" + str + "'");
	}

// public static class CommandExecutionEvent {
//
// private String command;
// private String errorMessage;
//
// public void setCommand(String command) {
// this.command = command;
// }
//
// public String getCommand() {
// return command;
// }
//
// public void setErrorMessage(String errorMessage) {
// this.errorMessage = errorMessage;
// }
//
// public String getErrorMessage() {
// return errorMessage;
// }
//
// public void clear() {
// command = null;
// errorMessage = null;
// }
//
// }

}
