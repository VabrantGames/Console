package com.vabrant.console.commandsections;

import com.vabrant.console.ConsoleCache;
import com.vabrant.console.SectionSpecifier;
import com.vabrant.console.SectionSpecifier.Builder.Rules;

public class DoubleArgument implements Argument, Parsable<Double> {
	
	public static SectionSpecifier createSpecifier() {
		return new SectionSpecifier.Builder()
				.specifiedSection(DoubleArgument.class)
				
				//e.g
				//100.
				//100.0
				//100.0d
				.addRule(Rules.DIGIT | Rules.ONE_OR_MORE)
				.addRule(Rules.CUSTOM, ".")
				.addRule(Rules.DIGIT | Rules.ZERO_OR_MORE)
				.addRule(Rules.CUSTOM | Rules.ONCE_OR_NONE, "dD")
				.or()
				
				.addRule(Rules.CUSTOM, ".")
				.addRule(Rules.DIGIT | Rules.ONE_OR_MORE)
				.addRule(Rules.CUSTOM | Rules.ONCE_OR_NONE, "dD")
				.build();
	}

	@Override
	public Double parse(ConsoleCache cache, String sectionText) throws RuntimeException {
		return Double.parseDouble(sectionText);
	}

}
