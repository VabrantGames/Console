
package com.vabrant.console.gui.panels;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.ui.VisUI;
import com.vabrant.console.gui.Panel;
import com.vabrant.console.log.Log;
import com.vabrant.console.log.LogManager;
import com.vabrant.console.log.LogManager.LogManagerEventListener;

public class LogPanel extends Panel {

	private Table logTable;
	private ScrollPane scrollPane;
	private LogManager logManager;

	public LogPanel (String name, LogManager logManager) {
		super(name);

		if (logManager == null) {
			throw new IllegalArgumentException("LogManager can't be null");
		}

		this.logManager = logManager;
		logTable = new Table();
		scrollPane = new ScrollPane(logTable, VisUI.getSkin());
		scrollPane.setFadeScrollBars(false);
		scrollPane.setScrollbarsOnTop(false);
		scrollPane.setSmoothScrolling(true);
		scrollPane.setOverscroll(false, false);

		logManager.subscribeToEvent(LogManager.ADD_LOG_EVENT, new LogManagerEventListener() {
			@Override
			public void handleEvent (LogManager logManager) {
				refresh();
			}
		});

		contentTable.add(scrollPane).grow();
	}

	public void refresh () {
		logTable.clear();
		logTable.add().grow().row();

		Array<Log> logs = logManager.getEntries();
		for (Log l : logs) {
			Label label = new Label(l.toSimpleString(), VisUI.getSkin());

			logTable.add(label).growX().top().left().row();
		}

		scrollPane.validate();
		scrollPane.setScrollPercentY(1);
	}
}
