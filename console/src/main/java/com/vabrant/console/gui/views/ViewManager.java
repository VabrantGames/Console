
package com.vabrant.console.gui.views;

import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Array;
import com.vabrant.console.ConsoleRuntimeException;
import com.vabrant.console.DebugLogger;
import com.vabrant.console.KeyboardScope;
import com.vabrant.console.events.EventManager;
import com.vabrant.console.gui.DefaultKeyboardScope;
import com.vabrant.console.gui.shortcuts.*;

public abstract class ViewManager extends DefaultView {

	private final int maxViews;
	protected View activeView;
	protected Array<View> views;
	protected EventManager eventManager;
	protected DefaultKeyMap managerKeyMap;
	protected TabManager tabManager;

	protected ViewManager (String name, Table rootTable) {
		this(name, rootTable, new Table(), null, 3);
	}

	protected ViewManager (String name, Table rootTable, Table contentTable, TabManager tabManager, int maxViews) {
		super(name, rootTable, contentTable);

		this.tabManager = tabManager;
		this.maxViews = maxViews;

		KeyboardScope managerScope = new DefaultKeyboardScope(this);
		keyboardScope = new ParentKeyboardScope(managerScope);

		managerKeyMap = new DefaultKeyMap(managerScope);
		keyMap = new ParentKeyMap(managerKeyMap);

		eventManager = new EventManager();
		views = new Array<>(maxViews);
		logger = new DebugLogger(name + " (View)", DebugLogger.NONE);

		if (this.tabManager == null) {
			this.tabManager = new DefaultTabManager();
		}

		this.tabManager.setViewManager(this);
		this.tabManager.init();
	}

	public Shortcut addShortcut (String ID, Runnable command, int... keybind) {
// return managerKeyMap.add(command, keys);
		return managerKeyMap.register(ID, command, keybind);
	}

	@Override
	public String getName () {
		return name;
	}

	public View getView (String name) {
		for (View v : views) {
			if (v.getName().equals(name)) return v;
		}
		return null;
	}

	public View getActiveView () {
		return activeView;
	}

	public TabManager getTabManager () {
		return tabManager;
	}

	public void addAllViews (View[] panels) {
		for (View p : panels) {
			addView(p);
		}
	}

	public void addView (View view) {
		if (views.size == maxViews) {
			throw new ConsoleRuntimeException("View limit reached");
		}

		if (view == null) {
			throw new IllegalArgumentException("View is null");
		}

		View existingView = getView(view.getName());
		if (existingView != null) {
			throw new ConsoleRuntimeException("View with name '" + view.getName() + "' already exists");
		}

		views.add(view);
		view.setViewManager(this);
		if (tabManager != null) tabManager.viewAdded(view);

		if (activeView == null) {
			setActiveView0(view);
		}
	}

	public void setActiveView (String name) {
		View panel = getView(name);

		if (panel == null) {
			logger.error("No panel with name '" + name + "' found for view " + getName());
			return;
		}

		setActiveView0(panel);
	}

	public void setActiveView (View view) {
		if (!views.contains(view, false)) return;
		setActiveView0(view);
	}

	public boolean hasView (View view) {
		return views.contains(view, false);
	}

	protected void setActiveView0 (View view) {
		if (activeView != null && activeView.equals(view)) return;

		activeView = view;
		contentTable.clearChildren();
		contentTable.add(activeView.getRootTable()).expand().fill();
		((ParentKeyboardScope)keyboardScope).setChild(activeView.getKeyboardScope());

		ParentKeyMap keyMap = (ParentKeyMap)super.keyMap;
		keyMap.setChild(activeView.getKeyMap());
		rootTable.invalidateHierarchy();
		if (tabManager != null) tabManager.setActiveView(view);
		contentTable.invalidateHierarchy();
	}

	public void previousView () {
		setActiveView(tabManager.nextView());
	}

	public void nextView () {
		setActiveView(tabManager.nextView());
	}

	public KeyMap getManagerKeyMap () {
		return managerKeyMap;
	}

	public Table getContentTable () {
		return contentTable;
	}

	public boolean hasFocus () {
		if (isHidden()) return false;
		return console.getFocusObject().equals(activeView);
	}

	@Override
	public void unfocus () {

	}

	public static class DefaultTabManager extends TabManager {

		private int viewIdx;

		@Override
		public void init () {

		}

		@Override
		public void viewAdded (View view) {

		}

		@Override
		public void setActiveView (View view) {

		}

		@Override
		public View nextView () {
			viewIdx = (++viewIdx) % viewManager.views.size;
			return viewManager.views.get(viewIdx);
		}

		@Override
		public View previousView () {
			viewIdx = (--viewIdx) % viewManager.views.size;
			return viewManager.views.get(viewIdx);
		}
	}

	public static abstract class TabManager {

		protected ViewManager viewManager;

		protected void setViewManager (ViewManager viewManager) {
			this.viewManager = viewManager;
		}

		public abstract void init ();

		public abstract void viewAdded (View view);

		public abstract void setActiveView (View view);

		public abstract View nextView ();

		public abstract View previousView ();
	}

}
