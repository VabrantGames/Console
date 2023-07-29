
package com.vabrant.console.gui;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

public class TableView extends DefaultView<Table> {

	public TableView (String name, Skin skin, Panel<?, ?>... panel) {
		this(name, new Table(), skin, panel);
	}

	public TableView (String name, Table table, Skin skin, Panel<?, ?>... panel) {
		super(name, table, skin, panel);
	}

	public TableView (String name, Table table, Skin skin, TableSetup rootTableSetup, int maxPanels, Panel<?, ?>... panels) {
		super(name, table, skin, rootTableSetup, maxPanels, panels);
	}

}
