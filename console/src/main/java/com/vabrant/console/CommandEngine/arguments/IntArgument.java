
package com.vabrant.console.CommandEngine.arguments;

public class IntArgument implements Argument {

	@Override
	public boolean isType (String s) {
		int idxToCheck = 0;

		if (s.charAt(0) == '-') {
			if (s.length() == 1) return false;
			idxToCheck = 1;
		}

		return Character.isDigit(s.charAt(idxToCheck)) && !s.contains(".") && Character.isDigit(s.charAt(s.length() - 1));
	}
}
