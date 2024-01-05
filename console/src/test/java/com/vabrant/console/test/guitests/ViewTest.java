
package com.vabrant.console.test.guitests;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.kotcrab.vis.ui.VisUI;
import com.vabrant.console.DebugLogger;
import com.vabrant.console.gui.*;
import com.vabrant.console.gui.commands.ToggleViewVisibilityCommand;
import com.vabrant.console.gui.shortcuts.DefaultKeyMap;
import com.vabrant.console.gui.views.DefaultView;
import com.vabrant.console.gui.views.DefaultViewConfiguration;
import com.vabrant.console.test.GUITestLauncher.WindowSize;

@WindowSize(width = 1080, height = 720)
public class ViewTest extends ApplicationAdapter {

	private GUIConsole console;

	@Override
	public void create () {
		Gdx.app.setLogLevel(Application.LOG_DEBUG);

		console = new DefaultGUIConsole();
		console.getLogger().setLevel(DebugLogger.DEBUG);

		DefaultViewConfiguration viewConfig = new DefaultViewConfiguration();

		KeyboardScope windowViewScope = new KeyboardScope("Window");
		WindowTestView windowView = new WindowTestView("Window", windowViewScope);
		console.addView(windowView);

		TableTestView tableView = new TableTestView("Table");
		console.addView(tableView);

		console.addGlobalShortcut(new ToggleViewVisibilityCommand(windowView), Keys.CONTROL_LEFT, Keys.NUM_1);
		console.addGlobalShortcut(new ToggleViewVisibilityCommand(tableView), Keys.NUM_2);
		console.addShortcut(windowViewScope, () -> System.out.println("Global window"), new int[] {Keys.M});

		Gdx.input.setInputProcessor(console.getInput());
	}

	@Override
	public void render () {
		console.draw();
	}

	private class WindowTestView extends DefaultView<Window, DefaultKeyMap> {
		private WindowTestView (String name, KeyboardScope scope) {
			super(name);
			rootTable = new Window(name, VisUI.getSkin());
			keyMap = new DefaultKeyMap(scope);
			rootTable.setTouchable(Touchable.childrenOnly);
			rootTable.add(new TextButton("Hello World", VisUI.getSkin()));
			keyMap.add(() -> System.out.println("Hello Window"), new int[] {Keys.SPACE});
		}
	}

	private class TableTestView extends DefaultView<Table, DefaultKeyMap> {
		private TableTestView (String name) {
			super(name);
			rootTable = new Table();
			rootTable.debugTable();
			rootTable.add(new TextButton("Hello World", VisUI.getSkin()));
			rootTable.pack();

			setWidthPercent(30);
			setHeightPercent(70);
		}
	}


}
