
package com.vabrant.console.CommandEngine.arguments;

public class ClassReferenceArgument implements Argument {

	@Override
	public boolean isType (String s) {
		return Character.isAlphabetic(s.charAt(0)) && !s.contains(".");
	}
}
