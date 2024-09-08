
package com.vabrant.console.commandexecutor;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pools;
import com.badlogic.gdx.utils.Queue;
import com.vabrant.console.events.CommandExecutorExecutionEvent;
import com.vabrant.console.commandexecutor.arguments.*;
import com.vabrant.console.commandexecutor.parsers.*;
import com.vabrant.console.ConsoleRuntimeException;
import com.vabrant.console.DebugLogger;
import com.vabrant.console.events.EventManager;
import com.vabrant.console.log.LogManager;

import static com.vabrant.console.Utils.STRING_BUILDER;

public class AdvancedCommandExecutor extends AbstractCommandExecutor<CommandCache> {

	private boolean printStackTrace;
	private CommandExecutorExecutionEvent event;
	private EventManager eventManager;

	public AdvancedCommandExecutor () {
		this(null);
	}

	public AdvancedCommandExecutor (LogManager logManager) {
		this(logManager, null);
	}

	public AdvancedCommandExecutor (LogManager logManager, CommandCache globalCache) {
		super(logManager, globalCache);

		eventManager = new EventManager(CommandExecutorExecutionEvent.class);

		arguments.addAll(new Argument[] {new IntArgument(), new DoubleArgument(), new FloatArgument(), new LongArgument(),
			new BooleanArgument(), new StringArgument(), new ClassReferenceArgument(), new MethodArgument()});

		parsers.put(DoubleArgument.class, new DoubleArgumentParser());
		parsers.put(IntArgument.class, new IntArgumentParser());
		parsers.put(FloatArgument.class, new FloatArgumentParser());
		parsers.put(LongArgument.class, new LongArgumentParser());
		parsers.put(StringArgument.class, new StringArgumentParser());
		parsers.put(BooleanArgument.class, new BooleanArgumentParser());
		parsers.put(ClassReferenceArgument.class, new ClassReferenceParser());
		parsers.put(MethodParser.class, new MethodParser());
	}

	public void printStackTrace (boolean printStackTrace) {
		this.printStackTrace = printStackTrace;
	}

	@Override
	public CommandExecutorResult execute (CommandCache cache, Object o) throws Exception {
		executionResult.clear();
		parserContext.setCache(cache);

		try {
			if (o instanceof String) {
				Array<MethodContainer> containers = null;
				String commandAsString = (String)o;
				Array<String> sections = createSections(commandAsString);
				containers = createAndParseContainersAndArguments(cache, sections);

				for (int i = containers.size - 1; i >= 0; i--) {
					containers.get(i).execute(null);
				}

				executionResult.setResult(containers.first().getReturnValue());
				executionResult.setExecutionStatus(true);

			} else if (o instanceof Object[]) {
				throw new ConsoleRuntimeException("Operation not supported");
			} else {
				throw new ConsoleRuntimeException("Operation not supported");
			}
		} catch (Exception e) {
			if (printStackTrace) e.printStackTrace();
			executionResult.setErrorString(e);
		}

		return executionResult;
	}

	private Array<String> createSections (String command) throws Exception {
		Array<String> strings = new Array<>();

		int start = 0;
		int len = command.length();
		int containersAmount = 0;
		char[] chars = command.toCharArray();
		boolean insideStringLiteral = false;
		char lastChar = 0x00;

		if (!Character.isAlphabetic(chars[0])) {
			throw new ConsoleRuntimeException("Command must start with a letter a-zA-z");
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
					throw new ConsoleRuntimeException("',' can only be used inside ()");
				}

				if (start == i) {
					// Check for double ",," in command
					if (lastChar == ',') {
						throw new ConsoleRuntimeException("Invalid use of ',' (Command) " + command.substring(0, i + 1));
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
					throw new ConsoleRuntimeException("Invalid use of ',' (Command) " + command.substring(0, i + 1));
				}

				skip = true;
				containersAmount++;
			case ')':
				if (lastChar == '(') {
					throw new ConsoleRuntimeException("'()' can't be empty");
				}

				if (!skip) containersAmount--;

				// Check for too many ')
				if (containersAmount < 0) {
					throw new ConsoleRuntimeException("Unnecessary ')'");
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
			throw new ConsoleRuntimeException("Missing ')'");
		}

		if (start < len) {
			strings.add(command.substring(start));
		}

		if (logger != null && logger.getLevel() == DebugLogger.DEBUG) {
			STRING_BUILDER.clear();
			STRING_BUILDER.append("[");

			for (int i = 0; i < strings.size; i++) {
				STRING_BUILDER.append(strings.get(i));

				if (i < (strings.size - 1)) {
					STRING_BUILDER.append(" , ");
				}
			}

			STRING_BUILDER.append("]");
			logger.debug("Sections", STRING_BUILDER.toString());
		}

		return strings;
	}

	private Array<MethodContainer> createAndParseContainersAndArguments (CommandCache cache, Array<String> sections)
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
					if (currentContainer.getCommand().getReturnType().equals(void.class)) {
						throw new ConsoleRuntimeException(
							"Method has no return type '" + currentContainer.getCommand().toString() + "'");
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

		if (logger != null && logger.getLevel() == DebugLogger.DEBUG) {
			STRING_BUILDER.clear();

			for (int i = 0; i < containers.size; i++) {
				MethodContainer c = containers.get(i);
				STRING_BUILDER.append("$C");
				STRING_BUILDER.append(i);
				STRING_BUILDER.append('=');
				STRING_BUILDER.append(c.getCommandName());
				STRING_BUILDER.append("(");

				for (int j = c.getArguments().size - 1; j >= 0; j--) {
					Object o = c.getArguments().get(j);

					if (o instanceof MethodContainer) {
						STRING_BUILDER.append("$C");
						STRING_BUILDER.append(containers.indexOf((MethodContainer)o, false));
					} else {
						STRING_BUILDER.append(o);
					}

					if (j > 0) {
						STRING_BUILDER.append(" , ");
					}
				}
				STRING_BUILDER.append(") ");
			}

			logger.debug("Containers", STRING_BUILDER.toString());
		}

		return containers;
	}

	private void parseContainer (String section, MethodContainer container) throws Exception {
		parserContext.setText(section);
		parserContext.setArgs(container.getArguments());
		container.setCommand((Command)parsers.get(MethodParser.class).parse(parserContext));
		container.setCommandName(section);
		parserContext.setArgs(null);
	}

	private void parseArgument (String str, MethodContainer container, Array<MethodContainer> containers) throws Exception {
		Object parsed = null;
		for (Argument a : arguments) {
			if (a.isType(str)) {
				if (a instanceof MethodArgument) {
					MethodContainer c = Pools.obtain(MethodContainer.class);
					parseContainer(str, c);

					if (c.getCommand().getReturnType().equals(void.class)) {
						throw new ConsoleRuntimeException("Method has no return type '" + c.getCommand().toString() + "'");
					}

					parsed = c;
					containers.add(c);
				} else {
					parserContext.setText(str);
					parsed = parsers.get(a.getClass()).parse(parserContext);
				}

				container.addArgument(parsed);
				break;
			}
		}
		if (parsed == null) throw new ConsoleRuntimeException("No argument type found for '" + str + "'");
	}
}
