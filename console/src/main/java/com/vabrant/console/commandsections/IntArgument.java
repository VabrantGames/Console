package com.vabrant.console.commandsections;

import com.vabrant.console.ConsoleCache;
import com.vabrant.console.SectionSpecifier;
import com.vabrant.console.SectionSpecifier.Builder.Rules;

public class IntArgument implements Argument, Parsable<Integer> {
	
	public static SectionSpecifier createSpecifier() {
		return new SectionSpecifier.Builder()
				.specifiedSection(IntArgument.class)
				
				//e.g 100
				.addRule(Rules.DIGIT | Rules.ONE_OR_MORE)
				.or()
				
				//e.g 0x64
				.addRule(Rules.EXPLICT, "0x")
				.addRule(Rules.CUSTOM | Rules.ONE_OR_MORE, "a-fA-F0-9")
				.or()
				
				//e.g #64
				.addRule(Rules.CUSTOM, "#")
				.addRule(Rules.CUSTOM | Rules.ONE_OR_MORE, "a-fA-F0-9")
				.or()
				
				//e.g 0b01100100
				.addRule(Rules.EXPLICT, "0b")
				.addRule(Rules.CUSTOM | Rules.ONE_OR_MORE, "01")
				.build();
	}

	@Override
	public Integer parse(ConsoleCache cache, String sectionString) throws RuntimeException {
		int value = 0;
		
		if(sectionString.startsWith("0b")) {
			value = Integer.parseInt(sectionString.substring(2), 2);
		}
		else if(sectionString.startsWith("0x")) {
			value = Integer.parseInt(sectionString.substring(2), 16);
		}
		else if(sectionString.startsWith("#")) {
			value = Integer.parseInt(sectionString.substring(1), 16);
		}
		else {
			value = Integer.parseInt(sectionString);
		}
		return value;
	}

}
