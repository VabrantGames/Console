
package com.vabrant.console.gui;

import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Window.WindowStyle;
import com.badlogic.gdx.scenes.scene2d.utils.BaseDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.kotcrab.vis.ui.VisUI;

public class TableView extends View<Table> {

	public TableView (String name, Panel panel) {
		this(name, new Table(), panel);
	}

	public TableView (String name, Table table, Panel panel) {
		super(name, table, panel);

		WindowStyle style = VisUI.getSkin().get(WindowStyle.class);
		if (style.background != null) {
			BaseDrawable d = new NinePatchDrawable((NinePatchDrawable)style.background);
			d.setPadding(0, 0, 0, 0);
			getRootTable().setBackground(d);
		}
	}

}
