
package com.vabrant.console.test.guitests;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.utils.ScreenUtils;
import com.vabrant.console.DebugLogger;
import com.vabrant.console.Utils;
import com.vabrant.console.gui.*;
import com.vabrant.console.gui.commands.ToggleViewVisibilityCommand;
import com.vabrant.console.gui.shortcuts.DefaultKeyMap;
import com.vabrant.console.gui.views.DefaultView;
import com.vabrant.console.test.GUITestLauncher.WindowSize;

@WindowSize(width = 1080, height = 720)
public class ViewTest extends ApplicationAdapter {

	private DefaultGUIConsole console;

	@Override
	public void create () {
		Gdx.app.setLogLevel(Application.LOG_DEBUG);

		console = new DefaultGUIConsole();
		console.getLogger().setLevel(DebugLogger.DEBUG);
		Skin skin = console.getSkin();

		WindowTestView windowView = new WindowTestView("Window", skin);
		console.addView(windowView);

		TableTestView tableView = new TableTestView("Table", skin);
		console.addView(tableView);

		console.addGlobalShortcut("Toggle Window View", new ToggleViewVisibilityCommand(windowView), Keys.NUM_2);
		console.addGlobalShortcut("Toggle Table View", new ToggleViewVisibilityCommand(tableView), Keys.NUM_3);
		console.addGlobalShortcut("Print Global Shortcut", () -> System.out.println("Global shortcut"), Keys.G);
		console.addShortcut("Print Global Window", windowView.getKeyboardScope(), () -> System.out.println("Global window"),
			Keys.M);

		Gdx.input.setInputProcessor(console.getInput());
	}

	@Override
	public void render () {
		ScreenUtils.clear(Color.WHITE);
		console.draw();
	}

	private class WindowTestView extends DefaultView {
		private WindowTestView (String name, Skin skin) {
			super(name, new Window(name, skin), new Table());

			Window w = (Window)rootTable;
			w.setResizable(true);
			keyboardScope = new DefaultKeyboardScope(this);
			keyMap = new DefaultKeyMap(keyboardScope);
// rootTable.setTouchable(Touchable.childrenOnly);
			contentTable.add(new TextButton("Hello World", skin));
			keyMap.register("Print Hello Window", () -> System.out.println("Hello Window"), Keys.SPACE);
			setPosition(Utils.CENTER);
		}
	}

	private class TableTestView extends DefaultView {
		private TableTestView (String name, Skin skin) {
			super(name, new Table(), new Table());

			rootTable.debugTable();
			contentTable.add(new TextButton("Hello World", skin));
			contentTable.pack();

// setWidthPercent(30);
// setHeightPercent(70);
		}
	}

}
