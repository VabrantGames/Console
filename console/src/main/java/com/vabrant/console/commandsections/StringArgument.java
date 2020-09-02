package com.vabrant.console.commandsections;

import com.vabrant.console.ConsoleCache;
import com.vabrant.console.SectionSpecifier;
import com.vabrant.console.SectionSpecifier.Builder.Rules;

public class StringArgument implements Argument, Parsable<String> {
	
	public static SectionSpecifier createSpecifier() {
		return new SectionSpecifier.Builder()
				.specifiedSection(StringArgument.class)
				
				.addRule(Rules.CUSTOM, "\"")
				.addRule(Rules.EXPLICT , ".*")
				.addRule(Rules.CUSTOM, "\"")
				.build();
	}

	@Override
	public String parse(ConsoleCache cache, String sectionText, Object extra) throws RuntimeException {
		return sectionText;
	}

}
