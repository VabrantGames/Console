
package com.vabrant.console.arguments;

public class BooleanArgument implements Argument {

	@Override
	public boolean isType (String s) {
		return s.equalsIgnoreCase("true") || s.equalsIgnoreCase("false");
	}
}
