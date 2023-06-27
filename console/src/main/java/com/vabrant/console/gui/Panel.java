
package com.vabrant.console.gui;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.kotcrab.vis.ui.widget.tabbedpane.Tab;

public abstract class Panel extends Tab {

	protected Table contentTable;
	private final String name;
	private final String scopeName;
	private View view;

	protected Panel(String name, String scopeName) {
		this(name, scopeName, new Table());
	}

	protected Panel (String name, String scopeName, Table table) {
		super(false, false);
		this.name = name;
		this.scopeName = scopeName;
        contentTable = table;
	}

	void setView(View view) {
		this.view = view;
	}

	public String getName() {
		return name;
	}

	@Override
	public String getTabTitle () {
		return name;
	}

	@Override
	public Table getContentTable () {
		return contentTable;
	}

    public final String getScopeName() {
        return scopeName;
    }

	public abstract void create (Skin skin);
}
