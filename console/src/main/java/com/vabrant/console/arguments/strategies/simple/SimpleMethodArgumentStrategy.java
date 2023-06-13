
package com.vabrant.console.arguments.strategies.simple;

import com.vabrant.console.arguments.Argument.ArgumentStrategy;

public class SimpleMethodArgumentStrategy implements ArgumentStrategy<String> {

	@Override
	public boolean isType (String s) {
		char c = s.charAt(0);
		return c == '.' || Character.isAlphabetic(c) && s.contains(".");
	}
}
