
package com.vabrant.console.commandextension.arguments;

public class LongArgument implements Argument {

	@Override
	public boolean isType (String s) {
		char lastChar = s.charAt(s.length() - 1);
		if (Character.isDigit(s.charAt(0)) && !s.contains(".")) {
			return lastChar == 'l' || lastChar == 'L';
		}
		return false;
	}
}
