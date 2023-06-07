
package com.vabrant.console.arguments.strategies.simple;

import com.vabrant.console.arguments.Argument;

public class SimpleStringArgumentStrategy implements Argument.ArgumentStrategy<String> {

	@Override
	public boolean isType (String s) {
		return s.charAt(0) == '"' && s.charAt(s.length() - 1) == '"';
	}
}
