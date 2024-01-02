package com.vabrant.console;

import com.badlogic.gdx.graphics.Color;
import com.vabrant.console.gui.shortcuts.ShortcutManager;
import space.earlygrey.shapedrawer.ShapeDrawer;
import space.earlygrey.shapedrawer.scene2d.ShapeDrawerDrawable;

public class Utils {

	public static ShapeDrawerDrawable createFilledRectangleDrawable (ShapeDrawer shapeDrawer, Color color) {
		return new ShapeDrawerDrawable(shapeDrawer) {
			@Override
			public void drawShapes (ShapeDrawer shapeDrawer, float x, float y, float width, float height) {
				shapeDrawer.filledRectangle(x, y, width, height, color);
			}
		};
	}

}
