
package com.vabrant.console.CommandEngine.arguments;

public class GlobalClassReferenceArgument implements Argument {

	public static String IDENTIFIER = "$";

	@Override
	public boolean isType (String s) {
		return s.startsWith(IDENTIFIER);
	}
}
