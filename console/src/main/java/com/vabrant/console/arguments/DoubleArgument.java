
package com.vabrant.console.arguments;

public class DoubleArgument implements Argument {
	@Override
	public boolean isType (String s) {
		if (Character.isDigit(s.charAt(0)) || s.charAt(0) == '.') {
			char lastChar = s.charAt(s.length() - 1);
			return lastChar == 'd' || lastChar == 'D';
		}
		return false;
	}
}
