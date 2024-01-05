package com.vabrant.console.gui;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.vabrant.console.gui.shortcuts.DefaultKeyMap;
import com.vabrant.console.gui.views.DefaultViewConfiguration;
import com.vabrant.console.log.LogManager;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class LogViewConfiguration extends DefaultViewConfiguration {

	public boolean displayLevelTag = true;
	public boolean displayLevelTextColoring = true;
	public LogManager logManager;

	public LogViewConfiguration (LogManager logManager, Skin skin, ShapeDrawer shapeDrawer) {
		super(skin, shapeDrawer);
		this.logManager = logManager;
	}
}
