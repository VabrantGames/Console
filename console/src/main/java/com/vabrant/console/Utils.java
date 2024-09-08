
package com.vabrant.console;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.utils.StringBuilder;
import com.vabrant.console.gui.views.View;
import space.earlygrey.shapedrawer.ShapeDrawer;
import space.earlygrey.shapedrawer.scene2d.ShapeDrawerDrawable;

import java.io.PrintWriter;
import java.io.StringWriter;

public class Utils {

	public static final int LEFT = 1 << 1;
	public static final int RIGHT = 1 << 2;
	public static final int TOP = 1 << 3;
	public static final int BOTTOM = 1 << 4;
	public static final int TOP_LEFT = TOP | LEFT;
	public static final int TOP_RIGHT = TOP | RIGHT;
	public static final int BOTTOM_LEFT = BOTTOM | LEFT;
	public static final int BOTTOM_RIGHT = BOTTOM | RIGHT;
	public static final int HORIZONTAL_CENTER = LEFT | RIGHT;
	public static final int VERTICAL_CENTER = TOP | BOTTOM;
	public static final int CENTER = HORIZONTAL_CENTER | VERTICAL_CENTER;

	public static final StringBuilder STRING_BUILDER = new StringBuilder(300);
	public static final Class[] EMPTY_ARGUMENT_TYPES = new Class[0];
	public static final Object[] EMPTY_ARGUMENTS = new Object[0];

	public static int setBit (int num, int bit, int setTo) {
		return (num & ~(1 << bit)) | (setTo & 1) << bit;
	}

	public static boolean isBitOn (int value, int bit) {
		return (value & (1 << (bit))) != 0;
	}

	public static ShapeDrawerDrawable createFilledRectangleDrawable (ShapeDrawer shapeDrawer, Color color) {
		return new ShapeDrawerDrawable(shapeDrawer) {
			@Override
			public void drawShapes (ShapeDrawer shapeDrawer, float x, float y, float width, float height) {
				shapeDrawer.filledRectangle(x, y, width, height, color);
			}
		};
	}

	public static boolean isWindow (View view) {
		if (view.getRootTable() == null) return false;
		return view.getRootTable() instanceof Window;
	}

	public static String printKeybind (int[] keybind) {
		StringBuilder builder = new StringBuilder();
		builder.append('[');

		for (int i = 0; i < keybind.length; i++) {

			switch (keybind[i]) {
			case Keys.GRAVE:
				builder.append("Grave");
				break;
			default:
				builder.append(Keys.toString(keybind[i]));
				break;
			}

			if (i < keybind.length - 1) builder.append(" ,");
		}

		builder.append("]");
		return builder.toString();
	}

	public static boolean areArgsEquals (Class arg, Class userArg, boolean exactArgs) {
		Class c1 = arg;
		Class c2 = userArg;

		if (c2 == null) return false;
		if (c1.equals(Object.class)) return true;

		if (c1.equals(int.class) || c1.equals(Integer.class)) {
			if (c2.equals(int.class) || c2.equals(Integer.class)) return true;
			return false;
		}

		if (c1.equals(float.class) || c1.equals(Float.class)) {
			if (c2.equals(float.class) || c2.equals(Float.class)) return true;
			if (!exactArgs && (c2.equals(int.class) || c2.equals(long.class) || c2.equals(Integer.class) || c2.equals(Long.class)))
				return true;
			return false;
		}

		if (c1.equals(double.class) || c1.equals(Double.class)) {
			if (c2.equals(double.class) || c2.equals(Double.class)) return true;
			if (!exactArgs && (c2.equals(int.class) || c2.equals(long.class) || c2.equals(float.class) || c2.equals(Float.class)
				|| c2.equals(Integer.class) || c2.equals(Long.class))) return true;
			return false;
		}

		if (c1.equals(long.class) || c1.equals(Long.class)) {
			if (c2.equals(long.class) || c2.equals(Long.class)) return true;
			if (!exactArgs && (c2.equals(int.class) || c2.equals(Integer.class))) return true;
			return false;
		}

		if (c1.equals(boolean.class) || c1.equals(Boolean.class)) {
			if (c2.equals(boolean.class) || c2.equals(Boolean.class)) return true;
			return false;
		}

		if (!c1.equals(c2)) return false;

		return true;
	}

	public static boolean areArgsEqual (Class[] args, Class[] userArgs, boolean exactArgs) {
		if (args.length == 0 && userArgs == null) return true;
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

			if (c1.isAssignableFrom(c2)) continue;

			if (!c1.equals(c2)) return false;
		}
		return true;
	}

	public static String exceptionToString (Throwable t) {
// StringWriter sw = new StringWriter();
		String asString = null;

		try (StringWriter sw = new StringWriter(); PrintWriter pw = new PrintWriter(sw)) {
			t.printStackTrace(pw);
			asString = sw.toString();
		} catch (Exception e) {
		}

		return asString;
	}

	public static String argumentsToString (Object[] args) {
		if (args.length == 0) {
			return "()";
		}

		StringBuilder builder = new StringBuilder();
		builder.append('(');
		for (int i = 0; i < args.length; i++) {
			builder.append(args[i].getClass().getSimpleName());
			if (i < (args.length - 1)) builder.append(", ");
		}
		builder.append(')');

		return builder.toString();
	}

	public static String argumentsToString (Class[] args) {
		if (args.length == 0) {
			return "()";
		}

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
