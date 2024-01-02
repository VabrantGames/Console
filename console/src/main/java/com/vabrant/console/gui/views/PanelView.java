
package com.vabrant.console.gui.views;

import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.kotcrab.vis.ui.widget.tabbedpane.Tab;
import com.vabrant.console.gui.KeyboardScope;
import com.vabrant.console.gui.shortcuts.KeyMap;

/**
 *
 */
public abstract class PanelView<T extends Table, U extends KeyMap> extends Tab implements View<T, U> {

	protected View<T, U> view;

	protected PanelView (View view) {
		super(true, false);
		this.view = view;
	}

	void setView (View view) {
		this.view = view;
	}

	public View getView () {
		return view;
	}

	@Override
	public KeyboardScope getKeyboardScope () {
		return view.getKeyboardScope();
	}

	@Override
	public String getName () {
		return view.getName();
	}

	@Override
	public String getTabTitle () {
		return view.getName();
	}

	@Override
	public Table getContentTable () {
		return view.getRootTable();
	}

	@Override
	public U getKeyMap () {
		return view.getKeyMap();
	}

	@Override
	public void focus () {
		view.focus();
	}

	@Override
	public void unfocus () {
		view.unfocus();
	}

	@Override
	public boolean lockFocus () {
		return view.lockFocus();
	}

	public static class PanelScope extends KeyboardScope {

		private final PanelView<?, ?> panel;

		public PanelScope (String name, PanelView<?, ?> panel) {
			super(name);
			this.panel = panel;
		}

		public PanelView getPanel () {
			return panel;
		}

	}
}
