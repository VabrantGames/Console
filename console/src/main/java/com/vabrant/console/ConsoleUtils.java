
package com.vabrant.console;

public class ConsoleUtils {

	public static final Class<?>[] EMPTY_ARGUMENT_TYPES = new Class[0];
	public static final Object[] EMPTY_ARGUMENTS = new Object[0];

	public static <T> T defaultIfNull (T t, T d) {
		return t != null ? t : d;
	}

	public static boolean areArgsEqual (Class[] args, Class[] userArgs) {
		if (args.length != userArgs.length) return false;

		for (int i = 0; i < args.length; i++) {
			Class c1 = args[i];
			Class c2 = userArgs[i];

			if (c2 == null) return false;
			if (c1.equals(Object.class)) continue;

			if (c1.equals(int.class)) {
				if (c2.equals(int.class) || c2.equals(Integer.class)) continue;
				return false;
			}

			if (c1.equals(float.class)) {
				if (c2.equals(float.class) || c2.equals(Float.class) || c2.equals(Integer.class) || c2.equals(Long.class)) continue;
				return false;
			}

			if (c1.equals(double.class)) {
				if (c2.equals(double.class) || c2.equals(Double.class) || c2.equals(Float.class) || c2.equals(Integer.class)
					|| c2.equals(Long.class)) continue;
				return false;
			}

			if (c1.equals(long.class)) {
				if (c2.equals(long.class) || c2.equals(Long.class) || c2.equals(Integer.class)) continue;
				return false;
			}

			if (c1.equals(boolean.class)) {
				if (c2.equals(boolean.class) || c2.equals(Boolean.class)) continue;
				return false;
			}

			if (!c1.equals(c2)) return false;
		}
		return true;
	}

}
