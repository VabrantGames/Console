package com.vabrant.console;

public class ConsoleUtils {

	public static boolean isEqual(String one, String two) {
		if(one == null || two == null) return false;
		if(Console.CASE_SENSITIVE) {
			return one.equals(two);
		}
		else {
			return one.equalsIgnoreCase(two);
		}
	}

}
