package com.vabrant.console;

public class ConsoleUtils {
	
	public static final String ADDED_TAG = "[Added]";
	public static final String REMOVE_TAG = "Removed";
	public static final String CONFLICT_TAG = "Conflict";
	public static final String ERROR_TAG = "Error";
	public static final Class[] EMPTY_ARGS = new Class[0];
	
	public static <T> T defaultIfNull(T t, T d) {
		return t != null ? t : d;
	}
	
	public static boolean equals(Class[] args, Class[] userArgs) {
		if(args.length != userArgs.length) return false;
		
		for(int i = 0; i < args.length; i++) {
			Class c1 = args[i];
			Class c2 = userArgs[i];
			if(c2 == null) return false;
			if(!c1.equals(c2)) return false;
		}
		return true;
	}

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
