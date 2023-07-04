package com.vabrant.console.gui;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.ui.VisUI;
import com.vabrant.console.log.Log;
import com.vabrant.console.log.LogManager;

    public class LogPanel extends Panel {

        private Table logTable;
    private ScrollPane scrollPane;
    private LogManager logManager;

    public LogPanel(String name, LogManager logManager) {
        super(name);
        this.logManager = logManager;
        logTable = new Table();
        scrollPane = new ScrollPane(logTable, VisUI.getSkin());
        scrollPane.setFadeScrollBars(false);
        scrollPane.setScrollbarsOnTop(false);
        scrollPane.setSmoothScrolling(true);
        scrollPane.setOverscroll(false, false);

        for (int i = 0; i < 50; i++) {
            logTable.add(new Label("Hello", VisUI.getSkin())).expand().fillX().row();
        }

        contentTable.add(scrollPane).expand().fill();
    }

    public void refresh() {
        logTable.clear();

        Array<Log> logs = logManager.getEntries();
//        for (Log : logs) {
//
//        }
    }
}
