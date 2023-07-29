
package com.vabrant.console.test.guitests;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.ScreenUtils;
import com.kotcrab.vis.ui.VisUI;
import com.vabrant.console.DebugLogger;
import com.vabrant.console.gui.GUIConsole;
import com.vabrant.console.gui.Panel;
import com.vabrant.console.gui.WindowView;
import com.vabrant.console.gui.commands.ToggleViewVisibilityCommand;
import com.vabrant.console.gui.shortcuts.DefaultKeyMap;

public class ViewTest extends ApplicationAdapter {

	public static void main (String[] args) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setWindowedMode(1080, 720);
		config.setTitle("ViewTest");
		new Lwjgl3Application(new ViewTest(), config);
	}

	private GUIConsole console;

	@Override
	public void create () {
		Gdx.app.setLogLevel(Application.LOG_DEBUG);

		console = new GUIConsole();
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
