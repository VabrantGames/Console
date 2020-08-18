package com.vabrant.console;

public class ConsoleUtils {
	
	public static final String ADDED_TAG = "[Added]";
	public static final String REMOVE_TAG = "Removed";
	public static final String CONFLICT_TAG = "Conflict";
	public static final String ERROR_TAG = "Error";
	public static final char NULL_CHARACTER = 0x00;
	public static final char[] RESERVED_CHARS = {' ', '.'};
	public static final Class<?>[] EMPTY_ARGUMENT_TYPES = new Class[0];
	public static final Object[] EMPTY_ARGUMENTS = new Object[0];
	
	public static <T> T defaultIfNull(T t, T d) {
		return t != null ? t : d;
	}
	
	public static boolean equals(Class[] args, Class[] userArgs) {
		if(args.length != userArgs.length) return false;
		
		for(int i = 0; i < args.length; i++) {
			Class c1 = args[i];
			Class c2 = userArgs[i];
			if(c2 == null) return false;
			
//			if(c1.equals(Object.class)) continue;
			if(c1.equals(int.class) && c2.equals(Integer.class)) continue;
			
			if(c1.equals(float.class)) {
				if(c2.equals(Float.class) || c2.equals(Integer.class) || c2.equals(Long.class)) continue;
			}
			
			if(c1.equals(double.class)) {
				if(c2.equals(Double.class) || c2.equals(Float.class) || c2.equals(Integer.class) || c2.equals(Long.class)) continue;
			}
			
			if(c1.equals(long.class)) {
				if(c2.equals(Long.class) || c2.equals(Integer.class)) continue;
			}
			
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
	
	public static boolean isReserved(char c) {
		for(char cc : RESERVED_CHARS) {
			if(cc == c) return true;
		}
		return false;
	}
	
	public static boolean isLegalName(String s) {
		if(s.isEmpty()) return false;
		return Character.isLetter(s.charAt(0));
	}

}
