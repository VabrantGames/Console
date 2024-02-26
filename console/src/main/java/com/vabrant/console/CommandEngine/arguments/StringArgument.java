
package com.vabrant.console.CommandEngine.arguments;

public class StringArgument implements Argument {

	@Override
	public boolean isType (String s) {
		return s.charAt(0) == '"' && s.charAt(s.length() - 1) == '"';
	}
}
