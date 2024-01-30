
package com.vabrant.console.test.guitests;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.ScreenUtils;
import com.vabrant.console.DebugLogger;
import com.vabrant.console.gui.DefaultGUIConsole;
import com.vabrant.console.gui.commands.ToggleViewVisibilityCommand;
import com.vabrant.console.gui.views.LogView;
import com.vabrant.console.log.LogLevel;
import com.vabrant.console.log.LogManager;
import com.vabrant.console.test.GUITestLauncher.WindowSize;
import space.earlygrey.shapedrawer.ShapeDrawer;

@WindowSize(width = 1080, height = 720)
public class LogViewTest extends ApplicationAdapter {

	private DefaultGUIConsole console;

	@Override
	public void create () {
		super.create();

		console = new DefaultGUIConsole();
		console.getLogger().setLevel(DebugLogger.DEBUG);

		Skin skin = console.getSkin();
		ShapeDrawer shapeDrawer = console.getShapeDrawer();
		LogManager manager = console.getLogManager();

		LogView tableView = LogView.createTableView("LogViewTable", manager, skin, shapeDrawer);
		console.addView(tableView);
		tableView.setPosition(100, 100);

		LogView windowView = LogView.createWindowView("LogWindow", manager, skin, shapeDrawer);
		windowView.displayLevelTag(false);
		console.addView(windowView);

		console.addGlobalShortcut("Toggle Table View", new ToggleViewVisibilityCommand(tableView), Keys.NUM_1);
		console.addGlobalShortcut("Toggle Window View", new ToggleViewVisibilityCommand(windowView), Keys.NUM_2);
		console.addGlobalShortcut("Create Info Log", () -> manager.add("Tag", "Info", LogLevel.INFO), Keys.NUM_7);
		console.addGlobalShortcut("Create Debug Log", () -> manager.add(null, "Debug", LogLevel.DEBUG), Keys.NUM_8);
		console.addGlobalShortcut("Create Error Log", () -> manager.add(null, "Error", LogLevel.ERROR), Keys.NUM_9);
		console.addGlobalShortcut("Create Normal Log", () -> manager.add(null, "Normal", LogLevel.NORMAL), Keys.NUM_0);

		Gdx.input.setInputProcessor(console.getInput());
	}

	@Override
	public void render () {
		ScreenUtils.clear(Color.WHITE);
		console.draw();
	}
}
