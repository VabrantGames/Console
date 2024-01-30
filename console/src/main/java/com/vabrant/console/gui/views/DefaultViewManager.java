
package com.vabrant.console.gui.views;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.vabrant.console.gui.shortcuts.Shortcut;

public class DefaultViewManager extends ViewManager {

	private String titleName;
	private Shortcut nextViewShortcut;
	private Shortcut previousViewShortcut;

	public DefaultViewManager (String name, Table rootTable) {
		this(name, rootTable, null, 5);
	}

	public DefaultViewManager (String name, Table rootTable, TabManager tabManager, int maxPanels) {
		this(name, rootTable, new Table(), tabManager, maxPanels);
	}

	public DefaultViewManager (String name, Table rootTable, Table contentTable, TabManager tabManager, int maxPanels) {
		super(name, rootTable, contentTable, tabManager, maxPanels);

		titleName = name;
		nextViewShortcut = addShortcut("NextView", this::nextView, Keys.PAGE_DOWN);
		previousViewShortcut = addShortcut("PreviousView", this::previousView, Keys.PAGE_UP);
	}

	public void setTitleName (String name) {
		this.titleName = name;
	}

	@Override
	protected void setActiveView0 (View view) {
		super.setActiveView0(view);

		String s = titleName + " - " + view.getName();

		if (rootTable instanceof Window) {
			Window w = (Window)rootTable;
			w.getTitleLabel().setText(s);
		} else if (titleBar != null) {
			titleBar.setTitle(s);
		}
	}
}
