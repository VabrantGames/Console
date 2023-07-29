
package com.vabrant.console.gui.panels;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.vabrant.console.gui.Panel;
import com.vabrant.console.gui.shortcuts.DefaultKeyMap;
import com.vabrant.console.log.LogManager;

public class LogPanel extends Panel<Table, DefaultKeyMap> {

	private final LogWidget widget;

	public LogPanel (String name, LogManager logManager, Skin skin) {
		super(name, Table.class, DefaultKeyMap.class);

		widget = new LogWidget(logManager, skin);

		contentTable.add(widget.getScrollPane()).grow();
	}

	public LogWidget getLogWidget () {
		return widget;
	}

	public void refresh () {
		widget.refresh();
	}
}
