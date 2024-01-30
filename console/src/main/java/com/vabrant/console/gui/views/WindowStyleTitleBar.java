
package com.vabrant.console.gui.views;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Window.WindowStyle;
import com.vabrant.console.ZeroPaddingDrawable;

public class WindowStyleTitleBar implements TitleBar {

	private Table table;
	private Label label;

	public WindowStyleTitleBar (Skin skin) {
		this(skin.get(WindowStyle.class));
	}

	public WindowStyleTitleBar (WindowStyle style) {
		table = new Table();
		table.setBackground(new ZeroPaddingDrawable(style.background));
		label = new Label("", new LabelStyle(style.titleFont, style.titleFontColor));
		label.setEllipsis(true);
		table.add(label).expandX().fillX().minWidth(0).padLeft(10);
		table.pack();
	}

	@Override
	public Table getTable () {
		return table;
	}

	@Override
	public void setTitle (String title) {
		label.setText(title);
	}

}
