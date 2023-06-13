
package com.vabrant.console.arguments;

public class InstanceReferenceArgument implements Argument {

	@Override
	public boolean isType (String s) {
		return Character.isAlphabetic(s.charAt(0)) && !s.contains(".");
	}
}
