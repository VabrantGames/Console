
package com.vabrant.console.commandexecutor.arguments;

public class GlobalClassReferenceArgument implements Argument {

	public static String IDENTIFIER = "$";

	@Override
	public boolean isType (String s) {
		return s.startsWith(IDENTIFIER);
	}
}
