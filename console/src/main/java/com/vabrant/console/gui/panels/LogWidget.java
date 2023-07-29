
package com.vabrant.console.gui.panels;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Array;
import com.vabrant.console.log.Log;
import com.vabrant.console.log.LogManager;

public class LogWidget {

	private Table logTable;
	private ScrollPane scrollPane;
	private LogManager logManager;
	private Skin skin;

	public LogWidget (LogManager logManager, Skin skin) {
		this.logManager = logManager;
		this.skin = skin;
		logTable = new Table();
		scrollPane = new ScrollPane(logTable, skin);
		scrollPane.setFadeScrollBars(false);
		scrollPane.setScrollbarsOnTop(false);
		scrollPane.setFlickScroll(false);
		scrollPane.setOverscroll(false, false);

		logManager.subscribeToEvent(LogManager.ADD_LOG_EVENT, manager -> refresh());
	}

	public ScrollPane getScrollPane () {
		return scrollPane;
	}

	public void refresh () {
		logTable.clear();
		logTable.add().grow().row();

		Array<Log> logs = logManager.getEntries();
		for (Log l : logs) {
			Label label = new Label(l.toSimpleString(), skin);

			logTable.add(label).growX().top().left().row();
		}

		scrollPane.validate();
		scrollPane.setScrollPercentY(1);
	}
}
