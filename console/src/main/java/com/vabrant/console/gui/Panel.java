
package com.vabrant.console.gui;

import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.kotcrab.vis.ui.widget.tabbedpane.Tab;
import com.vabrant.console.ConsoleRuntimeException;
import com.vabrant.console.DebugLogger;
import com.vabrant.console.gui.shortcuts.KeyMap;

/**
 *
 */
public abstract class Panel<T extends Table, U extends KeyMap> extends Tab implements FocusObject<U> {

	protected T contentTable;
	private final String name;
	protected DefaultView<?> view;
	protected U keyMap;
	protected PanelScope scope;
	protected final DebugLogger logger;

	protected Panel (String name, Class<T> contentTableClass, Class<U> keyMapClass) {
		super(true, false);

		if (name == null) {
			throw new IllegalArgumentException("Name can't be null");
		}

		this.name = name;
		scope = new PanelScope(name, this);
		logger = new DebugLogger(name + " (Panel)", DebugLogger.NONE);

		try {
			contentTable = ClassReflection.newInstance(contentTableClass);
		} catch (Exception e) {
			throw new ConsoleRuntimeException(e);
		}

		if (keyMapClass != null) {
			try {
				keyMap = (U)ClassReflection.getConstructor(keyMapClass, ConsoleScope.class).newInstance(scope);
			} catch (Exception e) {
				throw new ConsoleRuntimeException(e);
			}
		}
	}

	void setView (DefaultView<?> view) {
		this.view = view;
	}

	public DefaultView getView () {
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
	public U getKeyMap () {
		return keyMap;
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

	public static class PanelScope extends ConsoleScope {

		private final Panel<?, ?> panel;

		public PanelScope (String name, Panel<?, ?> panel) {
			super(name);
			this.panel = panel;
		}

		public Panel getPanel () {
			return panel;
		}

	}
}
