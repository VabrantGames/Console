package com.vabrant.console.commandsections;

import com.vabrant.console.ConsoleCache;
import com.vabrant.console.SectionSpecifier;
import com.vabrant.console.SectionSpecifier.Builder.Rules;

public class LongArgument implements Argument, Parsable<Long> {
	
	public static SectionSpecifier createSpecifier() {
		return new SectionSpecifier.Builder()
				.specifiedSection(LongArgument.class)
				.addRule(Rules.DIGIT | Rules.ONE_OR_MORE)
				.addRule(Rules.CUSTOM, "lL")
				.build();
	}

	@Override
	public Long parse(ConsoleCache cache, String sectionText, Object extra) throws RuntimeException {
		char c = sectionText.charAt(sectionText.length() - 1);
		if(c != 'l' && c != 'L') throw new RuntimeException("Error parsing Long");  
		return Long.parseLong(sectionText.substring(0, sectionText.length() - 1));
	}

}
