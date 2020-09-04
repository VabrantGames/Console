package com.vabrant.console;

import java.util.Stack;
import java.util.regex.Matcher;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.Pools;
import com.vabrant.console.commandsections.Argument;
import com.vabrant.console.commandsections.ContainerInfo;
import com.vabrant.console.commandsections.ContainerEndArgument;
import com.vabrant.console.commandsections.ArgumentGroupSeparatorArgument;
import com.vabrant.console.commandsections.ContainerStartArgument;
import com.vabrant.console.commandsections.CommandSection;
import com.vabrant.console.commandsections.ContainerArgument;
import com.vabrant.console.commandsections.DoubleArgument;
import com.vabrant.console.commandsections.Executable;
import com.vabrant.console.commandsections.FloatArgument;
import com.vabrant.console.commandsections.InstanceReferenceArgument;
import com.vabrant.console.commandsections.IntArgument;
import com.vabrant.console.commandsections.LongArgument;
import com.vabrant.console.commandsections.MethodArgument;
import com.vabrant.console.commandsections.MethodArgument.MethodArgumentInfo;
import com.vabrant.console.commandsections.Parsable;
import com.vabrant.console.commandsections.SpaceArgument;
import com.vabrant.console.commandsections.StringArgument;

public class CommandLine extends TextField {
	
	DebugLogger logger = new DebugLogger(CommandLine.class, DebugLogger.DEBUG);
	
	private final char nullChar = 0x00;
	private final char[] separators = {' ', '(', ',', ')'};
	private Array<SectionSpecifier> specifiers;
	private Matcher matcher;
	private ObjectMap<Class<?>, Argument> arguments;
	private Console console;
	
	public CommandLine(Console console, Skin skin) {
		super("", skin);
		
		this.console = console;
		
		arguments = new ObjectMap<>();
		arguments.put(MethodArgument.class, new MethodArgument());
		arguments.put(DoubleArgument.class, new DoubleArgument());
		arguments.put(FloatArgument.class, new FloatArgument());
		arguments.put(IntArgument.class, new IntArgument());
		arguments.put(LongArgument.class, new LongArgument());
		arguments.put(InstanceReferenceArgument.class, new InstanceReferenceArgument());
		arguments.put(StringArgument.class, new StringArgument());
		arguments.put(ContainerStartArgument.class, new ContainerStartArgument());
		arguments.put(ContainerEndArgument.class, new ContainerEndArgument());
		arguments.put(ArgumentGroupSeparatorArgument.class, new ArgumentGroupSeparatorArgument());
		arguments.put(SpaceArgument.class, new SpaceArgument());
		arguments.put(ContainerArgument.class, new ContainerArgument());
		
		addListener(new ClickListener() {
			@Override
			public boolean keyDown(InputEvent event, int keycode) {
				switch(keycode) {
					case Keys.ENTER:
						try {
							executeCommand();
						}
						catch(Exception e) {
							System.out.println("Error" + ": " + e.getMessage());
//						System.err.println(e.getMessage());
							e.printStackTrace();
						}
						break;
					case Keys.FORWARD_DEL:
						clearCommandLine();
						break;
				}
				
				if(keycode == Keys.ENTER) {
				}
				return super.keyDown(event, keycode);
			}
		});
		
		specifiers = new Array<>();
		createSpecifiers();
		matcher = specifiers.first().getPattern().matcher("");
	}
	
	private void clearCommandLine() {
		setText("");
	}

	private void createSpecifiers() {
		specifiers.add(MethodArgument.createSpecifier());
		specifiers.add(DoubleArgument.createSpecifier());
		specifiers.add(FloatArgument.createSpecifier());
		specifiers.add(IntArgument.createSpecifier());
		specifiers.add(LongArgument.createSpecifier());
		specifiers.add(InstanceReferenceArgument.createSpecifier());
		specifiers.add(StringArgument.createSpecifier());
	}
	
	private boolean isSeparator(char c) {
		for (int i = 0; i < separators.length; i++) {
			if (c == separators[i]) return true;
		}
		return false;
	}
	
	private void executeCommand() {
		if(text.isEmpty()) return;

		Array<CommandSection> sections = new Array<>();
		Array<CommandSection> containers = new Array<>();
		
		createSectionsAndArguments(sections);
		createContainers(sections, containers);
		parse(containers);

		logger.debug("ParsedSections");
		for(int i = 0; i < sections.size; i++) {
			StringBuilder builder = new StringBuilder();
			CommandSection section = sections.get(i);
			if(section.getArgumentType() instanceof Parsable) {
				if(!section.hasBeenParsed()) continue;
				
				builder.append((i + 1)).append(": ");
				builder.append("Text:[").append(section.getText()).append("] ");
				
				builder.append("ReturnType:");
				builder.append("[");
				builder.append(section.getReturnType().getSimpleName());
				builder.append("] ");
				
				if(section.getArgumentType() instanceof MethodArgument) {
					MethodArgumentInfo info = section.getMethodArgumentInfo();
					if(info != null) {
						builder.append("ArgLength:");
						builder.append('[');
						builder.append(info.getArgLength());
						builder.append("] ");
					}
				}
				
				logger.debug(builder.toString());
			}
		}

//		clearCommandLine();
	}
	
	private void parse(Array<CommandSection> containers) {
		for(int i = 0; i < containers.size; i++) {
			CommandSection container = containers.get(i);
			ContainerInfo containerInfo = container.getContainerInfo();
			CommandSection leadingSection = containerInfo.getLeadingSection();
			
			if(leadingSection != null && leadingSection.getArgumentType() instanceof MethodArgument) {
				leadingSection.setMethodArgumentInfo(Pools.obtain(MethodArgumentInfo.class));
			}
			
			Array<Array<CommandSection>> arguments = containerInfo.getArguments();
			for(int j = arguments.size - 1; j >= 0; j--) {
				Array<CommandSection> argument = arguments.get(j);

				MethodArgumentInfo leadingFragmentArgumentInfo = null;
				
				if(argument.first().getArgumentType() instanceof MethodArgument) {
					leadingFragmentArgumentInfo = Pools.obtain(MethodArgumentInfo.class);
				}

				for(int k = argument.size - 1; k >= 0; k--) {
					CommandSection fragment = argument.get(k);
					
					if(fragment.getArgumentType() instanceof Parsable) {
						if(fragment.getArgumentType() instanceof MethodArgument) {
							MethodArgumentInfo info = null;
							
							if(k == 0) {
								info = leadingFragmentArgumentInfo;
							}
							else {
								info = Pools.obtain(MethodArgumentInfo.class);
							}
							
							info = ((Parsable<MethodArgumentInfo>)fragment.getArgumentType()).parse(console.getCache(), fragment.getText(), info);
							fragment.setMethodArgumentInfo(info);
							fragment.setReturnType(info.getMethodInfo().getReturnType());
							fragment.setHasBeenParsed(true);
						}
						else {
							fragment.setReturnObject(((Parsable<?>)fragment.getArgumentType()).parse(console.getCache(), fragment.getText(), null));
							fragment.setHasBeenParsed(true);
						}
						
						if(leadingFragmentArgumentInfo != null && k > 0) {
							leadingFragmentArgumentInfo.addArgumentSection(fragment);
						}
					}
				}
				
				if(leadingSection != null && leadingSection.getArgumentType() instanceof MethodArgument) {
					leadingSection.getMethodArgumentInfo().addArgumentSection(argument.first());
				}
			}
			
			if(leadingSection != null && leadingSection.getArgumentType() instanceof MethodArgument) {
				MethodArgumentInfo info = leadingSection.getMethodArgumentInfo();
				info = ((Parsable<MethodArgumentInfo>)leadingSection.getArgumentType()).parse(console.getCache(), leadingSection.getText(), info);
				leadingSection.setMethodArgumentInfo(info);
				leadingSection.setReturnType(info.getMethodInfo().getReturnType());
				leadingSection.setHasBeenParsed(true);
				
				container.setReturnType(info.getMethodInfo().getReturnType());
			}
		}
		
	}

	private void createContainers(Array<CommandSection> sections, Array<CommandSection> containers) {
		Stack<CommandSection> nestedContainerStack = new Stack<>();
		
		ContainerInfo currentInfo = null;
		CommandSection previousSection = null;
		
		int containerNum = 0;
		for(int i = 0; i < sections.size; i++){
			CommandSection section = sections.get(i);
			
			if(section.getArgumentType() instanceof ContainerStartArgument) {
				CommandSection container = Pools.obtain(CommandSection.class);
				container.setArgumentType(arguments.get(ContainerArgument.class));
				
				ContainerInfo info = new ContainerInfo(previousSection != null && previousSection.getArgumentType() instanceof Executable ? previousSection : null);
				container.setContainerInfo(info);
				currentInfo = info;
				
				nestedContainerStack.add(container);
				
				//Look ahead and check if the next section's type is a method argument with an argument group
				//If so skip this section because we are interested in the output of method and its arguments not just the method.
				int nextSectionOffset = 1;
				CommandSection lookAheadSection = (i + nextSectionOffset) < sections.size ? sections.get(i + nextSectionOffset) : null;
				
				if(lookAheadSection != null) {
					if(lookAheadSection.getArgumentType() instanceof SpaceArgument && (i + 2) < sections.size) lookAheadSection = sections.get(i + ++nextSectionOffset);
					if(lookAheadSection.getArgumentType() instanceof MethodArgument) {
						if((i + (nextSectionOffset + 1)) < sections.size && sections.get(i + ++nextSectionOffset).getArgumentType() instanceof ContainerStartArgument) {
							previousSection = lookAheadSection;
							i += --nextSectionOffset;
							continue;
						}
					}
				}
			}
			else if(section.getArgumentType() instanceof ContainerEndArgument) {
				CommandSection completedContainer = nestedContainerStack.pop();
				completedContainer.setText("Container " + containerNum++);
				containers.add(completedContainer);
				
				//If this container is nested inside another container pass it in as an argument to that container
				if(nestedContainerStack.size() > 0) {
					currentInfo = nestedContainerStack.peek().getContainerInfo();
					currentInfo.addArgumentFragment(completedContainer);
				}
			}
			else if(currentInfo != null && !(section.getArgumentType() instanceof SpaceArgument)) {
				currentInfo.addArgumentFragment(section);
			}
			
			previousSection = section;
		}
		
		if(logger.getLogLevel() == DebugLogger.DEBUG) {
			StringBuilder builder = new StringBuilder(100);
			builder.append("Create Containers").append("\n");
			
			for(int i = 0; i < containers.size; i++) {
				CommandSection container = containers.get(i);
				
				ContainerInfo info = container.getContainerInfo();
	
				builder.append("\t").append("Container ").append(i).append(":").append("\n");
				builder.append("\t\t").append("Leading Section: ");
				if(info.getLeadingSection() != null) {
					CommandSection leadingSection = info.getLeadingSection();
					builder.append("[").append(leadingSection.getText()).append("] ");
					builder.append('[').append(leadingSection.getArgumentType().getClass().getSimpleName()).append(']');
				}
				else {
					builder.append("null");
				}
				
				builder.append("\n");
				
				Array<Array<CommandSection>> arguments = info.getArguments();
				for(int j = 0; j < arguments.size; j++) {
					builder.append("\t\t").append("Argument ").append(j).append(':').append("\n");
					
					Array<CommandSection> argumentFragments = arguments.get(j);
					for(int k = 0; k < argumentFragments.size; k++) {
						CommandSection fragment = argumentFragments.get(k);
						builder.append("\t\t\t").append("Fragment ").append(k).append(": ");
						builder.append('[').append(fragment.getText()).append(']').append(' ');
						builder.append("ArgType:[").append(fragment.getArgumentType() == null ? "null" : fragment.getArgumentType().getClass().getSimpleName()).append(']');
						builder.append("\n");
					}
				}
				
			}
			logger.debug(builder.toString());
		}
	}

	private void createSectionsAndArguments(Array<CommandSection> sections){
		int start = 0;
		boolean insideStringLiteral = false;
		final String commandText = text.trim();
		
		for(int i = 0; i < commandText.length(); i++) {
			char c = commandText.charAt(i);
			char nextC = (i + 1) > (commandText.length() - 1) ? nullChar : commandText.charAt(i + 1);
			
			if(c == ' ' && nextC == ' ') continue;
			if(c == '"') insideStringLiteral = !insideStringLiteral;
			
			if(!insideStringLiteral) {
				if(isSeparator(c)) {
					CommandSection section = Pools.obtain(CommandSection.class);
					sections.add(section);
					
					switch(c) {
						case ' ':
							section.setText(" ");
							section.setArgumentType(arguments.get(SpaceArgument.class));
							break;
						case '(':
							section.setText("(");
							section.setArgumentType(arguments.get(ContainerStartArgument.class));
							break;
						case ')':
							section.setText(")");
							section.setArgumentType(arguments.get(ContainerEndArgument.class));
							break;
						case ',':
							section.setText(",");
							section.setArgumentType(arguments.get(ArgumentGroupSeparatorArgument.class));
							break;
					}
					
					start++;
				}
				else if(isSeparator(nextC) || i == (commandText.length() - 1)) {
					CommandSection section = Pools.obtain(CommandSection.class);
					section.setText(commandText.substring(start, i + 1).trim());
					sections.add(section);
					
					start = i + 1;
	
					//Find argument
					for(SectionSpecifier spec : specifiers) {
						matcher.usePattern(spec.getPattern());
						matcher.reset(section.getText());
	
						if(matcher.matches()) {
							section.setArgumentType(arguments.get(spec.getSpecifiedSectionClass()));
							break;
						}
					}
					
					if(section.getArgumentType() == null) throw new RuntimeException("Error executing command");
				}
			}
		}
		
		if(logger.getLogLevel() == DebugLogger.DEBUG) {
			System.out.println();
			StringBuilder builder = new StringBuilder(100);
			builder.append("Create Sections And Arguments").append('\n');
			builder.append('\t').append("Command: ").append(commandText).append('\n');
			
			for(int i = 0; i < sections.size; i++) {
				CommandSection section = sections.get(i);
				builder.append("\t\t").append("Section ").append(i).append(": ").append("[").append(section.getText()).append("] ");
				String spec = section.getArgumentType() == null ? "null" : section.getArgumentType().getClass().getSimpleName();
				builder.append("[").append(spec).append("]").append("\n");
			}

			logger.debug(builder.toString());
		}

	}

}
