
package com.vabrant.console.gui;

import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.kotcrab.vis.ui.widget.tabbedpane.Tab;
import com.vabrant.console.DebugLogger;
import com.vabrant.console.gui.shortcuts.DefaultKeyMap;

public abstract class Panel extends Tab {

	protected Table contentTable;
	private final String name;
	private View<?> view;
	protected DefaultKeyMap keyMap;
	protected PanelScope scope;
	private DebugLogger logger;

	protected Panel (String name) {
		this(name, new Table());
	}

	protected Panel (String name, Table table) {
		super(false, false);
		this.name = name;
		contentTable = table;
		scope = new PanelScope(name, this);
		keyMap = new DefaultKeyMap(scope);
		logger = new DebugLogger(name + " (Panel)", DebugLogger.DEBUG);
	}

	void setView (View<?> view) {
		this.view = view;
	}

	public View getView () {
		return view;
	}

	public ConsoleScope getScope () {
		return scope;
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

	public DefaultKeyMap getKeyMap () {
		return keyMap;
	}

	public void focus () {
		GUIConsole console = view.getConsole();
// console.getShortcutManager().setPanelKeyMap(keyMap);
		console.setScope(scope);
		logger.info("Focus");
	}

	public void unfocus () {
		GUIConsole console = view.getConsole();
// console.getShortcutManager().setPanelKeyMap(null);
// console.getShortcutManager().setKeycodeFilter(null);
// console.setScope("");
		console.removeScope(scope);
		logger.info("Unfocus");
	}

	private class PanelScope extends ConsoleScope {

		private final Panel panel;

		PanelScope (String name, Panel panel) {
			super(name);
			this.panel = panel;
		}

		@Override
		public boolean isActive () {
			if (view.isHidden()) return false;
			return view.getPanel().equals(panel);
		}
	}
}
