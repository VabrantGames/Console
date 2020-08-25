package com.vabrant.console;

import java.util.Iterator;
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
import com.vabrant.console.commandsections.ArgumentGroupEndArgument;
import com.vabrant.console.commandsections.ArgumentGroupInfo;
import com.vabrant.console.commandsections.ArgumentGroupSeparatorArgument;
import com.vabrant.console.commandsections.ArgumentGroupStartArgument;
import com.vabrant.console.commandsections.CommandSection;
import com.vabrant.console.commandsections.DoubleArgument;
import com.vabrant.console.commandsections.Executable;
import com.vabrant.console.commandsections.FloatArgument;
import com.vabrant.console.commandsections.InstanceReferenceArgument;
import com.vabrant.console.commandsections.IntArgument;
import com.vabrant.console.commandsections.LongArgument;
import com.vabrant.console.commandsections.MethodArgument;
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
		arguments.put(ArgumentGroupStartArgument.class, new ArgumentGroupStartArgument());
		arguments.put(ArgumentGroupEndArgument.class, new ArgumentGroupEndArgument());
		arguments.put(ArgumentGroupSeparatorArgument.class, new ArgumentGroupSeparatorArgument());
		arguments.put(SpaceArgument.class, new SpaceArgument());

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
		createSectionsAndArguments(sections);
		createGroups(sections);
		
		//Create basic information
//		parseSections(sections);

//		createArgumentGroups(sections);
//		CommandSection leadExecutableSection = sections.removeIndex(0);
//		
//		createAndExecuteMethodArguments(sections);
//		
//		Object[] argumentObjects = createArgumentArray(sections);
//		
//		Executable executable = (Executable) arguments.get(MethodArgument.class);
//		Object returnObject = executable.execute(leadExecutableSection.getArgumentObject(), argumentObjects);
//		if(returnObject != null) System.out.println(returnObject.toString());
//		clearCommandLine();
	}
	
	private void createGroups(Array<CommandSection> sections) {
		Array<CommandSection> newSections = new Array<>();
		Stack<CommandSection> groupSectionStack = new Stack<>();
		
		ArgumentGroupInfo currentInfo = null;
		CommandSection previousSection = null;
		
		int groupNum = 0;
		for(int i = 0; i < sections.size; i++){
			CommandSection section = sections.get(i);
			
			if(section.getArgumentType() instanceof ArgumentGroupStartArgument) {
				CommandSection groupCommandSection = Pools.obtain(CommandSection.class);
				groupCommandSection.setText("Group " + groupNum++);
				
				ArgumentGroupInfo sInfo = new ArgumentGroupInfo(previousSection != null && previousSection.getArgumentType() instanceof Executable ? previousSection : null);
				groupCommandSection.setArgumentGroupInfo(sInfo);
				currentInfo = sInfo;
				
				groupSectionStack.add(groupCommandSection);
				
				//Look ahead and check if the next section's type is a method argument with an argument group
				//If so skip this section because we are interested in the output of method and its arguments not just the method.
				int nextSectionOffset = 1;
				CommandSection lookAheadSection = (i + nextSectionOffset) < sections.size ? sections.get(i + nextSectionOffset) : null;
				
				if(lookAheadSection != null) {
					if(lookAheadSection.getArgumentType() instanceof SpaceArgument && (i + 2) < sections.size) lookAheadSection = sections.get(i + ++nextSectionOffset);
					if(lookAheadSection.getArgumentType() instanceof MethodArgument) {
						if((i + (nextSectionOffset + 1)) < sections.size && sections.get(i + ++nextSectionOffset).getArgumentType() instanceof ArgumentGroupStartArgument) {
							previousSection = lookAheadSection;
							i += --nextSectionOffset;
							continue;
						}
					}
				}
			}
			else if(section.getArgumentType() instanceof ArgumentGroupEndArgument) {
				CommandSection completedSection = groupSectionStack.pop();
				newSections.add(completedSection);
				
				//If this argument group is nested inside another group pass it in as an argument to its parent
				if(groupSectionStack.size() > 0) {
					currentInfo = groupSectionStack.peek().getArgumentGroupInfo();
					currentInfo.addSectionForArgument(completedSection);
				}
			}
			else if(currentInfo != null && !(section.getArgumentType() instanceof SpaceArgument)) {
				currentInfo.addSectionForArgument(section);
			}
			
			previousSection = section;
		}
		
		System.out.println();
		for(CommandSection cs : newSections) {
			ArgumentGroupInfo csInfo = cs.getArgumentGroupInfo();

			StringBuilder builder = new StringBuilder(50);
			builder.append("Owning Executable: ");
			if(csInfo.getOwningExecutable() != null) {
				CommandSection owning = csInfo.getOwningExecutable();
				builder.append('[').append(owning.getText()).append("] ");
				builder.append('[').append(owning.getArgumentType().getClass().getSimpleName()).append(']');
			}
			else {
				builder.append("null");
			}
			
			builder.append("\n\t");
			
			Array<Array<CommandSection>> arguments = csInfo.getArguments();
			for(int i = 0; i < arguments.size; i++) {
				builder.append("Argument ").append(i).append(':');
				builder.append("\n\t\t");
				
				Array<CommandSection> argumentSections = arguments.get(i);
				for(int j = 0; j < argumentSections.size; j++) {
					CommandSection section = argumentSections.get(j);
					builder.append("Section ").append(j).append(": ");
					builder.append('[').append(section.getText()).append(']').append(' ');
					builder.append('[').append(section.getArgumentType() == null ? "null" : section.getArgumentType().getClass().getSimpleName()).append(']');
					builder.append("\n\t\t");
				}
			}
			
			logger.debug(builder.toString());
		}
		
	}
	
//	public Object[] createArgumentArray(Array<CommandSection> sections) {
//		Object[] o = new Object[sections.size];
//		int index = 0;
//		for(CommandSection s : sections) {
//			o[index++] = s.getArgumentObject();
//		}
//		return o;
//	}

	private void createAndExecuteMethodArguments(Array<CommandSection> sections) {
		for(int i = 0; i < sections.size; i++) {
			CommandSection section = sections.get(i);
			if(section.getArgumentType() instanceof MethodArgument) {
				Executable exe = (Executable) arguments.get(MethodArgument.class);
				section.setArgumentObject(exe.execute(section.getArgumentObject()));
			}
		}
	}
	
	private void checkForLeadingMethodArgument(Array<CommandSection> sections) {
		//Check if first section is a method
		if(sections.get(0).getArgumentType() instanceof MethodArgument) return;
		
		if(sections.size < 2 || !(sections.get(0).getArgumentType() instanceof InstanceReferenceArgument)) throw new RuntimeException("Command cannot be executed.");

		//e.g object .method (ClassReferenceArgument, MethodArgument) -> object.method (MethodArgument)
		//e.g object object.method is not executable
		if(sections.get(1).getArgumentType() instanceof MethodArgument) {
			CommandSection section = sections.get(1);
			if(section.getText().charAt(0) != '.') throw new RuntimeException("Command cannot be executed.");
			
			CommandSection newMethodSection = Pools.obtain(CommandSection.class);
			newMethodSection.setArgument(arguments.get(MethodArgument.class));
			newMethodSection.setText(sections.get(0).getText() + sections.get(1).getText());
			sections.removeRange(0, 1);
			
			sections.insert(0, newMethodSection);
			return;
		}
		//e.g object object (ClassReferenceArgument, ClassReferenceArgument) -> object.object (MethodArgument)
		else if(sections.get(1).getArgumentType() instanceof InstanceReferenceArgument) {
			CommandSection newMethodSection = Pools.obtain(CommandSection.class);
			newMethodSection.setArgument(arguments.get(MethodArgument.class));
			newMethodSection.setText(sections.get(0).getText() + '.' + sections.get(1).getText());
			
			//Remove the sections we combined
			sections.removeRange(0, 1);
			
			sections.insert(0, newMethodSection);
			return;
		}
		
		throw new RuntimeException("Command cannot be executed");
	}
	
	private void createSectionsAndArguments(Array<CommandSection> sections){
		int start = 0;
		boolean insideStringLiteral = false;
		final String s = text.trim();
		
		for(int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			char nextC = (i + 1) > (s.length() - 1) ? nullChar : s.charAt(i + 1);
			
			if(c == ' ' && nextC == ' ') continue;
			if(c == '"') insideStringLiteral = !insideStringLiteral;
			
			if(!insideStringLiteral) {
				if(isSeparator(c)) {
//					if(c == ' ') continue;
					
					CommandSection section = Pools.obtain(CommandSection.class);
					sections.add(section);
					
					switch(c) {
						case ' ':
							section.setText(" ");
							section.setArgument(arguments.get(SpaceArgument.class));
							break;
						case '(':
							section.setText("(");
							section.setArgument(arguments.get(ArgumentGroupStartArgument.class));
							break;
						case ')':
							section.setText(")");
							section.setArgument(arguments.get(ArgumentGroupEndArgument.class));
							break;
						case ',':
							section.setText(",");
							section.setArgument(arguments.get(ArgumentGroupSeparatorArgument.class));
							break;
					}
					
					start++;
				}
				else if(isSeparator(nextC) || i == (s.length() - 1)) {
					CommandSection section = Pools.obtain(CommandSection.class);
					section.setText(s.substring(start, i + 1).trim());
					sections.add(section);
					
					start = i + 1;
	
					//Find argument
					for(SectionSpecifier spec : specifiers) {
						matcher.usePattern(spec.getPattern());
						matcher.reset(section.getText());
	
						if(matcher.matches()) {
							section.setArgument(arguments.get(spec.getSpecifiedSectionClass()));
							break;
						}
					}
					
					if(section.getArgumentType() == null) throw new RuntimeException("Error executing command");
				}
			}
		}
		
		logger.debug("Create Basic Arguments");
		logger.debug("Full Command: " + s);
		for(int i = 0; i < sections.size; i++) {
			StringBuilder builder = new StringBuilder(50);
			CommandSection section = sections.get(i);
			builder.append((i + 1) + ":");
			builder.append(" [" + section.getText() + ']');
			
			String spec = section.getArgumentType() == null ? "null" : section.getArgumentType().getClass().getSimpleName();
			builder.append(" [" + spec + ']');
			logger.debug(builder.toString());
		}
	}
	
	private void parseSections(Array<CommandSection> sections) {
		for(int i = 0; i < sections.size; i++) {
			CommandSection section = sections.get(i);
			Argument argument = section.getArgumentType();
			if(argument instanceof Parsable) {
				Object o = ((Parsable)argument).parse(console.getCache(), section.getText());
				section.setArgumentObject(o);
			}
		}
	}

}
