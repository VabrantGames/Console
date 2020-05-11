package com.vabrant.console.commandsections;

import java.util.Iterator;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectSet;
import com.vabrant.console.MethodReference;
import com.vabrant.console.CommandObject;
import com.vabrant.console.Console;

public class ExecutableObjectArgument implements ExecutableArgument {

	private static final Class[] EMPTY_ARG_TYPE = new Class[0];
	
	private boolean alreadyExecuted;
	private boolean findCommandObjectDuringExecution;
	private boolean hasObject;
	private boolean hasMethod;
	private String methodName;
	private CommandObject commandObject;
	private ObjectSet<CommandObject> commandObjectsWithMethodName;
	private final Array<ArgumentSection> argumentSections = new Array<>(2);
	private Class returnType;
	private Object argument;

	@Override
	public void set(Console console, String sectionString) throws Exception {
		if(sectionString.contains(".")) {
			//method with object omitted
			if(sectionString.charAt(0) == '.') {
				sectionString = sectionString.substring(1, sectionString.length());
				if(!console.hasMethod(sectionString)) throw new IllegalArgumentException("Console doesn't contain method " + sectionString);
				commandObjectsWithMethodName = console.getCommandObjectsThatHaveMethod(sectionString);
				findCommandObjectDuringExecution = true;
				methodName = sectionString;
			}
			else {
				String[] parts = sectionString.split("\\.");
				
				if(parts.length > 2) throw new IllegalArgumentException("Invalid number of arguments");

				commandObject = console.getCommandObject(parts[0]);
				if(commandObject == null) throw new IllegalArgumentException("Object " + parts[0] + " doesn't exist");
				
				if(!commandObject.containsMethod(parts[1])) throw new IllegalArgumentException("Method " + parts[1] + " doesn't exists");
				methodName = parts[1];
			}
		}
		else {
			throw new IllegalArgumentException("Invalid ExecutableObjectArgument format");
		}
	}
	
	public boolean hasObject() {
		return findCommandObjectDuringExecution ? true : hasObject;
	}
	
	public boolean hasMethod() {
		return hasMethod;
	}
	
	public void addArgument(ArgumentSection arg) {
		argumentSections.add(arg);
	}

	@Override
	public Class getArgumentType() {
		return returnType;
	}

	@Override
	public Object getArgument() {
		return argument;
	}
	
	@Override
	public void reset() {
		alreadyExecuted = false;
		findCommandObjectDuringExecution = false;
		hasObject = false;
		hasMethod = false;
		methodName = null;
		commandObject = null;
		commandObjectsWithMethodName = null;
		returnType = null;
		argument = null;
		argumentSections.clear();
	}

	@Override
	public void execute() throws Exception {
		if (alreadyExecuted) return;

		Class[] argTypes = EMPTY_ARG_TYPE;
		Object[] args = null;

		//execute any executable arguments
		if (argumentSections.size > 0) {
			for(int i = 0; i < argumentSections.size; i++) {
				ArgumentSection argS = argumentSections.get(i);
				
				if(!argS.isValid()) throw new RuntimeException("Invalid section: " + argS.getSectionString());
				
				Argument a = argS.getArgument();
				if(a instanceof ExecutableArgument) {
					((ExecutableArgument)a).execute();
				}
			}
			
			argTypes = new Class[argumentSections.size];

			//save the classes of all the arguments to be used to look up a method later
			for (int i = 0; i < argumentSections.size; i++) {
				argTypes[i] = argumentSections.get(i).getArgument().getArgumentType();
				
				if(argTypes[i] == void.class) {
					String m = "void " + (commandObject == null ? "?" : commandObject.getName()) + "." + methodName;
					throw new RuntimeException("Argument return type can't be void - " + m);
				}
			}
		}

		MethodReference method = null;
		if (findCommandObjectDuringExecution) {
			Iterator<CommandObject> iterator = commandObjectsWithMethodName.iterator();
			CommandObject entry = null;
			while (iterator.hasNext()) {
				entry = iterator.next();
				method = entry.getMethod(methodName, argTypes);
				if (method != null) {
					commandObject = entry;
					break;
				}
			}
		}
		else {
			method = commandObject.getMethod(methodName, argTypes);
		}

		if (method == null) throw new RuntimeException("Method does not exist.");

		returnType = method.getReturnType();

		if (argTypes != null) {
			args = new Object[argumentSections.size];

			for (int i = 0; i < argumentSections.size; i++) {
				Argument section = argumentSections.get(i).getArgument();
				args[i] = section.getArgument();
			}
		}

		argument = method.invoke(commandObject.getObject(), args);

		alreadyExecuted = true;
	}

}
