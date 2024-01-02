package com.vabrant.console.gui;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.vabrant.console.gui.shortcuts.DefaultKeyMap;
import com.vabrant.console.gui.views.DefaultViewConfiguration;
import com.vabrant.console.log.LogManager;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class LogViewConfiguration<T extends Table> extends DefaultViewConfiguration<T, DefaultKeyMap> {

	public boolean displayLevelTag = true;
	public boolean displayLevelTextColoring = true;
	public LogManager logManager;

	public LogViewConfiguration (T rootTable, LogManager logManager, Skin skin, ShapeDrawer shapeDrawer) {
		super(rootTable, skin, shapeDrawer);
		this.logManager = logManager;
	}
}
