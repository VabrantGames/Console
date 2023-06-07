
package com.vabrant.console.arguments.strategies.pattern;

import com.vabrant.console.arguments.Argument;

import java.util.regex.Pattern;

import static com.vabrant.console.arguments.strategies.pattern.PatternBuilder.CUSTOM;
import static com.vabrant.console.arguments.strategies.pattern.PatternBuilder.EXPLICT;

public class PatternStringArgumentStrategy implements Argument.ArgumentStrategy<PatternStrategyInput> {

	private Pattern pattern;

	public PatternStringArgumentStrategy () {
		pattern = PatternBuilder.getInstance().addRule(CUSTOM, "\"").addRule(EXPLICT, ".*").addRule(CUSTOM, "\"").build();
	}

	@Override
	public boolean isType (PatternStrategyInput patternStrategyInput) {
		return false;
	}
}
