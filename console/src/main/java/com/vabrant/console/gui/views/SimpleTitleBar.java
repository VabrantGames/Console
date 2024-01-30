
package com.vabrant.console.gui.views;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.vabrant.console.Utils;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class SimpleTitleBar implements TitleBar {

	private final Table table;
	private final Label label;

	public SimpleTitleBar (Skin skin, ShapeDrawer shapeDrawer) {
		this(skin, shapeDrawer, Color.DARK_GRAY, Color.WHITE);
	}

	public SimpleTitleBar (Skin skin, ShapeDrawer shapeDrawer, Color barColor, Color fontColor) {
		table = new Table();
		table.setBackground(Utils.createFilledRectangleDrawable(shapeDrawer, barColor));
		LabelStyle style = new LabelStyle(skin.get(LabelStyle.class));
		style.fontColor = fontColor;
		label = new Label("", style);
		label.setEllipsis(true);
		table.add(label).expandX().fillX().minWidth(0).padLeft(10);
	}

	@Override
	public Table getTable () {
		return table;
	}

	@Override
	public void setTitle (String title) {
		label.setText(title);
		label.validate();
	}

}
