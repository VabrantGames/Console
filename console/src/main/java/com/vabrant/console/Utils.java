
package com.vabrant.console;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.vabrant.console.gui.views.View;
import space.earlygrey.shapedrawer.ShapeDrawer;
import space.earlygrey.shapedrawer.scene2d.ShapeDrawerDrawable;

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

}
