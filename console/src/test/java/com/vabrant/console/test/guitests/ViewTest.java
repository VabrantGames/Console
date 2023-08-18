
package com.vabrant.console.test.guitests;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.ScreenUtils;
import com.kotcrab.vis.ui.VisUI;
import com.vabrant.console.DebugLogger;
import com.vabrant.console.gui.DefaultGUIConsole;
import com.vabrant.console.gui.GUIConsole;
import com.vabrant.console.gui.Panel;
import com.vabrant.console.gui.WindowView;
import com.vabrant.console.gui.commands.ToggleViewVisibilityCommand;
import com.vabrant.console.gui.shortcuts.DefaultKeyMap;
import com.vabrant.console.test.GUITestLauncher.WindowSize;

@WindowSize(width = 1080, height = 720)
public class ViewTest extends ApplicationAdapter {

	private GUIConsole console;

	@Override
	public void create () {
		Gdx.app.setLogLevel(Application.LOG_DEBUG);

		console = new DefaultGUIConsole();
		console.getLogger().setLevel(DebugLogger.DEBUG);

		TestPanel helloPanel = new TestPanel("Hello");
		TestPanel worldPanel = new TestPanel("World");

		DefaultKeyMap map = helloPanel.getKeyMap();

		WindowView view = new WindowView("TestWindow", VisUI.getSkin(), helloPanel);
		view.getLogger().setLevel(DebugLogger.DEBUG);
// view.setShowTabbedPane(false);
		console.addView(view);

		console.addShortcut(new ToggleViewVisibilityCommand(view), new int[] {Keys.NUM_1});
		console.addShortcut( () -> {
// view.setShowTabbedPane(true);
		}, new int[] {Keys.NUM_2});

		Gdx.input.setInputProcessor(console.getInput());
	}

	@Override
	public void render () {
		ScreenUtils.clear(Color.WHITE);
		console.draw();
	}

	private static class TestPanel extends Panel<Table, DefaultKeyMap> {

		TestPanel (String name) {
			super(name, Table.class, DefaultKeyMap.class);
		}
	}

}
