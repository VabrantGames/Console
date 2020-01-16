package com.vabrant.console;

public class ConsoleUtils {

	public static boolean isEqual(ConsoleSettings settings, String one, String two) {
		if(one == null || two == null) return false;
		if(settings.caseSensitive) {
			return one.equals(two);
		}
		else {
			return one.equalsIgnoreCase(two);
		}
	}

}
