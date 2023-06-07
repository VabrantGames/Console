
package com.vabrant.console.arguments.strategies.simple;

import com.vabrant.console.arguments.Argument;

public class SimpleInstanceReferenceArgumentStrategy implements Argument.ArgumentStrategy<String> {

	@Override
	public boolean isType (String s) {
		return Character.isAlphabetic(s.charAt(0)) && !s.contains(".");
	}
}
