package com.vabrant.console.commandsections;

import com.vabrant.console.ConsoleCache;
import com.vabrant.console.SectionSpecifier;
import com.vabrant.console.SectionSpecifier.Builder.Rules;

public class FloatArgument implements Argument, Parsable<Float> {

	public static SectionSpecifier createSpecifier() {
		return new SectionSpecifier.Builder()
				.specifiedSection(FloatArgument.class)
				
				//e.g 100f
				.addRule(Rules.DIGIT | Rules.ONE_OR_MORE)
				.addRule(Rules.CUSTOM, "fF")
				.or()
				
				//e.g .100f
				.addRule(Rules.CUSTOM, ".")
				.addRule(Rules.DIGIT | Rules.ONE_OR_MORE)
				.addRule(Rules.CUSTOM, "fF")
				.or()
				
				//e.g 100.0f
				.addRule(Rules.DIGIT | Rules.ONE_OR_MORE)
				.addRule(Rules.CUSTOM, ".")
				.addRule(Rules.DIGIT | Rules.ZERO_OR_MORE)
				.addRule(Rules.CUSTOM, "fF")
				.build();
	}

	@Override
	public Float parse(ConsoleCache cache, String text, Object extra) throws RuntimeException {
		return Float.parseFloat(text);
	}

}
