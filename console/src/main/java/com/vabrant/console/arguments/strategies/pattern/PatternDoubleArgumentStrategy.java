
package com.vabrant.console.arguments.strategies.pattern;

import com.vabrant.console.arguments.Argument;

import java.util.regex.Pattern;

import static com.vabrant.console.arguments.strategies.pattern.PatternBuilder.*;

public class PatternDoubleArgumentStrategy implements Argument.ArgumentStrategy<PatternStrategyInput> {

	private Pattern pattern;

	public PatternDoubleArgumentStrategy () {
		pattern = PatternBuilder.getInstance()
			// e.g
			// 100.
			// 100.0
			// 100.0d
			.addRule(DIGIT | ONE_OR_MORE).addRule(CUSTOM, ".").addRule(DIGIT | ZERO_OR_MORE).addRule(CUSTOM | ONCE_OR_NONE, "dD")
			.or()

			.addRule(CUSTOM, ".").addRule(DIGIT | ONE_OR_MORE).addRule(CUSTOM | ONCE_OR_NONE, "dD").build();
	}

	@Override
	public boolean isType (PatternStrategyInput patternStrategyInput) {
		return false;
	}
}
