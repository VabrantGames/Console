
package com.vabrant.console.arguments.strategies.simple;

import com.vabrant.console.arguments.Argument;

public class SimpleLongArgumentStrategy implements Argument.ArgumentStrategy<String> {

	@Override
	public boolean isType (String s) {
		if (Character.isDigit(s.charAt(0)) && !s.contains(".")) {
			char lastChar = s.charAt(s.length() - 1);
			return lastChar == 'l' || lastChar == 'L';
		}
		return false;
	}
}
