
package com.vabrant.console.gui;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.kotcrab.vis.ui.widget.tabbedpane.Tab;
import com.vabrant.console.DebugLogger;

public abstract class Panel extends Tab {

	protected Table contentTable;
	private final String name;
	private View<?> view;
	protected KeyMap keyMap;
	private DebugLogger logger;

	protected Panel (String name) {
		this(name, new Table());
	}

	protected Panel (String name, Table table) {
		super(false, false);
		this.name = name;
		contentTable = table;
		keyMap = new KeyMap(name);
		logger = new DebugLogger(name + " (Panel)", DebugLogger.DEBUG);
	}

	void setView (View<?> view) {
		this.view = view;
	}

	public View getView () {
		return view;
	}

	public String getName () {
		return name;
	}

	@Override
	public String getTabTitle () {
		return name;
	}

	@Override
	public Table getContentTable () {
		return contentTable;
	}

	public KeyMap getKeyMap () {
		return keyMap;
	}

	public void focus () {
		GUIConsole console = view.getConsole();
		console.getShortcutManager().setPanelKeyMap(keyMap);
		console.setScope(name);
		logger.info("Focus");
	}

	public void unfocus () {
		GUIConsole console = view.getConsole();
		console.getShortcutManager().setPanelKeyMap(null);
		console.getShortcutManager().setKeycodeFilter(null);
		console.setScope("");
		logger.info("Unfocus");
	}
}
