
package com.vabrant.console.commandextension;

public class CommandUtils {

	public static final Class<?>[] EMPTY_ARGUMENT_TYPES = new Class[0];
	public static final Object[] EMPTY_ARGUMENTS = new Object[0];

	public static <T> T defaultIfNull (T t, T d) {
		return t != null ? t : d;
	}

	public static boolean exact (Class[] args, Class[] userArgs) {
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
				if (c2.equals(float.class) || c2.equals(Float.class)) continue;
				return false;
			}

			if (c1.equals(double.class)) {
				if (c2.equals(double.class) || c2.equals(Double.class)) continue;
				return false;
			}

			if (c1.equals(long.class)) {
				if (c2.equals(long.class) || c2.equals(Long.class)) continue;
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

	public static boolean areArgsEqual (Class[] args, Class[] userArgs) {
		return areArgsEqual(args, userArgs, false);
	}

	public static boolean areArgsEqual (Class[] args, Class[] userArgs, boolean exactArgs) {
		if (args.length != userArgs.length) return false;

		for (int i = 0; i < args.length; i++) {
			Class c1 = args[i];
			Class c2 = userArgs[i];

			if (c2 == null) return false;
			if (c1.equals(Object.class)) continue;

			if (c1.equals(int.class) || c1.equals(Integer.class)) {
				if (c2.equals(int.class) || c2.equals(Integer.class)) continue;
				return false;
			}

			if (c1.equals(float.class) || c1.equals(Float.class)) {
				if (c2.equals(float.class) || c2.equals(Float.class)) continue;
				if (!exactArgs
					&& (c2.equals(int.class) || c2.equals(long.class) || c2.equals(Integer.class) || c2.equals(Long.class))) continue;
				return false;
			}

			if (c1.equals(double.class) || c1.equals(Double.class)) {
				if (c2.equals(double.class) || c2.equals(Double.class)) continue;
				if (!exactArgs && (c2.equals(int.class) || c2.equals(long.class) || c2.equals(float.class) || c2.equals(Float.class)
					|| c2.equals(Integer.class) || c2.equals(Long.class))) continue;
				return false;
			}

			if (c1.equals(long.class) || c1.equals(Long.class)) {
				if (c2.equals(long.class) || c2.equals(Long.class)) continue;
				if (!exactArgs && (c2.equals(int.class) || c2.equals(Integer.class))) continue;
				return false;
			}

			if (c1.equals(boolean.class) || c1.equals(Boolean.class)) {
				if (c2.equals(boolean.class) || c2.equals(Boolean.class)) continue;
				return false;
			}

			if (!c1.equals(c2)) return false;
		}
		return true;
	}

	public static String argsToString (Class<?>[] args) {
		if (args.length == 0) return "()";

		StringBuilder builder = new StringBuilder();

		builder.append('(');
		for (int i = 0; i < args.length; i++) {
			builder.append(args[i].getSimpleName());
			if (i < (args.length - 1)) builder.append(", ");
		}
		builder.append(')');

		return builder.toString();
	}

}
