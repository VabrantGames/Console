package com.vabrant.console.commandsections;

import com.badlogic.gdx.utils.Array;

public class ArgumentGroupExecutable implements Executable {

	@Override
	public Object execute(Object... executableInfo) throws RuntimeException {
		ArgumentGroupInfo info = (ArgumentGroupInfo) executableInfo[0];
		
		Array<Array<CommandSection>> arguments = info.getArguments();
		
		//Where the executed arguments will be put 
		Object[] executedArguments = new Object[arguments.size];
		
		//Loop throw the arguments
		int argumentIndex = 0;
		for(Array<CommandSection> argumentSections : arguments) {
//			if(argumentSections.size > 1 && !(argumentSections.first().getArgument() instanceof MethodArgument)) throw new RuntimeException("Invalid arguments supplied");
			
			//The section that will   
			CommandSection owningSection = argumentSections.first().getArgumentType() instanceof MethodArgument ? argumentSections.removeIndex(0) : null;
			
			if(argumentSections.size > 0) {
				//create an object representation of the data
				Object[] argumentSectionsArgumentObjects = new Object[argumentSections.size];
				for(int i = 0; i < argumentSections.size; i++) {
					CommandSection s = argumentSections.get(i);
					
//					if(s.getArgumentType() instanceof MethodArgument) s.set
					
					argumentSectionsArgumentObjects[i] = argumentSections.get(i).getArgumentObject();
				}
			}
	
			if(argumentSections.first().getArgumentType() instanceof MethodArgument) {
				MethodArgument exe = (MethodArgument) argumentSections.first().getArgumentType();
//				executedArguments[argumentIndex++] = exe.execute(argumentSections.first().getArgumentObject(), argumentSectionsArgumentObjects);
			}
			else {
//				executedArguments[argumentIndex++] = argumentSectionsArgumentObjects[0];
			}
		}
		
		
		
		return null;
	}

}
