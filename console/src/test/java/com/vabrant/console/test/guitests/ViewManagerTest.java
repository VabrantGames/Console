
package com.vabrant.console.test.guitests;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.ui.Window.WindowStyle;
import com.badlogic.gdx.utils.ScreenUtils;
import com.kotcrab.vis.ui.Sizes;
import com.kotcrab.vis.ui.widget.tabbedpane.Tab;
import com.kotcrab.vis.ui.widget.tabbedpane.TabbedPane;
import com.kotcrab.vis.ui.widget.tabbedpane.TabbedPane.TabbedPaneStyle;
import com.kotcrab.vis.ui.widget.tabbedpane.TabbedPaneListener;
import com.vabrant.console.DebugLogger;
import com.vabrant.console.Utils;
import com.vabrant.console.ZeroPaddingDrawable;
import com.vabrant.console.gui.DefaultGUIConsole;
import com.vabrant.console.gui.DefaultKeyboardScope;
import com.vabrant.console.gui.commands.ToggleViewVisibilityCommand;
import com.vabrant.console.gui.shortcuts.DefaultKeyMap;
import com.vabrant.console.gui.views.*;
import com.vabrant.console.gui.views.ViewManager.TabManager;
import com.vabrant.console.log.LogLevel;
import com.vabrant.console.log.LogManager;
import com.vabrant.console.test.GUITestLauncher.WindowSize;
import space.earlygrey.shapedrawer.ShapeDrawer;

@WindowSize(width = 1080, height = 720)
public class ViewManagerTest extends ApplicationAdapter {

	private DefaultGUIConsole console;

	@Override
	public void create () {
		super.create();

		console = new DefaultGUIConsole();
		console.getLogger().setLevel(DebugLogger.DEBUG);

		Skin skin = console.getSkin();
		ShapeDrawer shapeDrawer = console.getShapeDrawer();

// DefaultViewManager v = new DefaultViewManager("ViewManager", new Window("Hello", skin), new Table(), null, 5);

		createTableViewManager(skin, shapeDrawer);
		createWindowViewManager(skin, shapeDrawer);
		Gdx.input.setInputProcessor(console.getInput());
	}

	private void createTableViewManager (Skin skin, ShapeDrawer shapeDrawer) {
		WindowStyle style = skin.get(WindowStyle.class);
		DefaultViewManager tableViewManager = new DefaultViewManager("Table", new Table(), null, 5);
		tableViewManager.getRootTable().setBackground(new ZeroPaddingDrawable(style.background));
		tableViewManager.setPosition(Utils.TOP_LEFT);
		tableViewManager.setSizePercent(20, 80);
		tableViewManager.translate(5, -5);
		tableViewManager.setTitleBar(new WindowStyleTitleBar(skin));
		console.addView(tableViewManager);
		console.addGlobalShortcut("Toggle Table View Manager", new ToggleViewVisibilityCommand(tableViewManager), Keys.NUM_1);

		ButtonView buttonView = new ButtonView("Button One", skin, shapeDrawer);
		tableViewManager.addView(buttonView);

		LogManager logManager = console.getLogManager();

		for (int i = 0; i < 50; i++) {
			logManager.add(null, "hello", LogLevel.DEBUG);
		}

		LogView logView = LogView.createTableView("LogView", logManager, skin, shapeDrawer);
		logView.setTitleBar(new SimpleTitleBar(skin, shapeDrawer));
		tableViewManager.addView(logView);
		console.addGlobalShortcut("Open Log View", new ToggleViewVisibilityCommand(tableViewManager, logView, true), Keys.NUM_8);

		tableViewManager.addView(new ButtonView("Bob", skin, shapeDrawer));
		tableViewManager.addView(new ButtonView("SomeSupeerLonngTittle fjwojfiow fiowjwoifjw", skin, shapeDrawer));
	}

	private void createWindowViewManager (Skin skin, ShapeDrawer shapeDrawer) {
		DefaultViewManager windowViewManager = new DefaultViewManager("Window", new Window("Window", skin), null, 5);
		((Window)windowViewManager.getRootTable()).setResizable(true);
		windowViewManager.setPosition(Utils.CENTER);
		console.addView(windowViewManager);
		console.addGlobalShortcut("Toggle Window View Manager", new ToggleViewVisibilityCommand(windowViewManager, true),
			Keys.NUM_2);

		windowViewManager.addView(new ButtonView("Hello Window", skin, shapeDrawer));
		windowViewManager.addView(new ButtonView("What is a window?", skin, shapeDrawer));
		windowViewManager.addView(new ButtonView("Window button", skin, shapeDrawer));
	}

	@Override
	public void resize (int width, int height) {
		console.resize(width, height);
	}

	@Override
	public void render () {
		ScreenUtils.clear(Color.WHITE);
		console.draw();
	}

	private class ButtonView extends DefaultView {

		ButtonView (String name, Skin skin, ShapeDrawer shapeDrawer) {
			super(name, new Table(), new Table());

			keyboardScope = new DefaultKeyboardScope(this);
			keyMap = new DefaultKeyMap(keyboardScope);
			keyMap.register("Print ButtonView", () -> System.out.println("ButtonView"), Keys.B);
			TextButton button = new TextButton(name, skin);
			button.getLabel().setEllipsis(true);
			contentTable.add(button).expand().center();
		}
	}

	private class VisUITabManager extends TabManager implements TabbedPaneListener {

		private TabbedPane pane;

		VisUITabManager (Skin skin) {
			pane = new TabbedPane(skin.get(TabbedPaneStyle.class), skin.get(Sizes.class));
			pane.getTabsPane().setDraggable(null);
			pane.addListener(this);
		}

		@Override
		public void init () {
			Table rootTable = viewManager.getRootTable();
// rootTable.add(pane.getTabsPane()).height(pane.getTabsPane().getPrefHeight()).row();
// pane.getTabsPane().validate();
			rootTable.add(pane.getTabsPane()).growX().row();
		}

		@Override
		public void viewAdded (View panel) {
			pane.add(new ViewWrapper(panel));
		}

		@Override
		public void setActiveView (View panel) {
		}

		@Override
		public View nextView () {
			return null;
		}

		@Override
		public View previousView () {
			return null;
		}

		@Override
		public void switchedTab (Tab tab) {
			viewManager.setActiveView(((ViewWrapper)tab).view);
		}

		@Override
		public void removedTab (Tab tab) {

		}

		@Override
		public void removedAllTabs () {

		}

		class ViewWrapper extends Tab {

			View view;

			ViewWrapper (View view) {
				this.view = view;
			}

			@Override
			public String getTabTitle () {
				return view.getName();
			}

			@Override
			public Table getContentTable () {
				return view.getRootTable();
			}
		}
	}
}
