
package com.vabrant.console.gui;

import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.kotcrab.vis.ui.widget.tabbedpane.Tab;
import com.vabrant.console.DebugLogger;
import com.vabrant.console.gui.shortcuts.DefaultKeyMap;
import com.vabrant.console.gui.shortcuts.KeyMap;

public abstract class Panel extends Tab implements FocusObject {

	protected Table contentTable;
	private final String name;
	protected View<?> view;
	protected KeyMap keyMap;
	protected PanelScope scope;
	protected DebugLogger logger;

	protected Panel (String name) {
		this(name, new Table(), null);
	}

	protected Panel (String name, Table table, KeyMap keyMap) {
		super(false, false);
		this.name = name;
		contentTable = table;
		scope = new PanelScope(name, this);
		this.keyMap = keyMap == null ? new DefaultKeyMap(scope) : keyMap;
		logger = new DebugLogger(name + " (Panel)", DebugLogger.NONE);
	}

	void setView (View<?> view) {
		this.view = view;
	}

	public View getView () {
		return view;
	}

	@Override
	public ConsoleScope getScope () {
		return scope;
	}

	@Override
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

	@Override
	public <T extends KeyMap> T getKeyMap () {
		return (T)keyMap;
	}

	@Override
	public void focus () {
		logger.info("Focus");
	}

	@Override
	public void unfocus () {
		logger.info("Unfocus");
	}

	@Override
	public boolean lockFocus () {
		return false;
	}

	private class PanelScope extends ConsoleScope {

		private final Panel panel;

		PanelScope (String name, Panel panel) {
			super(name);
			this.panel = panel;
		}

	}
}
