
package com.vabrant.console.CommandEngine.arguments;

public class FloatArgument implements Argument {

	@Override
	public boolean isType (String s) {
		int idxToCheck = 0;

		if (s.charAt(0) == '-') {
			if (s.length() == 1) return false;
			idxToCheck = 1;
		}

		if (Character.isDigit(s.charAt(idxToCheck)) || s.charAt(idxToCheck) == '.') {
			char lastChar = s.charAt(s.length() - 1);
			return lastChar == 'f' || lastChar == 'F' || s.contains(".") && Character.isDigit(lastChar);
		}
		return false;
	}

}
