
package com.vabrant.console.arguments.strategies.simple;

import com.vabrant.console.arguments.Argument;

public class SimpleBooleanArgumentStrategy implements Argument.ArgumentStrategy<String> {

	@Override
	public boolean isType (String s) {
		return s.equalsIgnoreCase("true") || s.equalsIgnoreCase("false");
	}
}
