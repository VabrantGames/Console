
package com.vabrant.console.commandexecutor.arguments;

public class LongArgument implements Argument {

	@Override
	public boolean isType (String s) {
		int idxToCheck = 0;

		if (s.charAt(0) == '-') {
			if (s.length() == 0) return false;
			idxToCheck = 1;
		}

		char lastChar = s.charAt(s.length() - 1);
		if (Character.isDigit(s.charAt(idxToCheck)) && !s.contains(".")) {
			return lastChar == 'l' || lastChar == 'L';
		}
		return false;
	}
}
