
package com.vabrant.console.arguments;

public class FloatArgument implements Argument {

	@Override
	public boolean isType (String s) {
		if (Character.isDigit(s.charAt(0)) || s.charAt(0) == '.') {
			char lastChar = s.charAt(s.length() - 1);
			return lastChar == 'f' || lastChar == 'F' || s.contains(".") && Character.isDigit(lastChar);
		}
		return false;
	}

}
