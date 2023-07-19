
package com.vabrant.console.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.ObjectMap;
import com.kotcrab.vis.ui.widget.tabbedpane.Tab;
import com.kotcrab.vis.ui.widget.tabbedpane.TabbedPane;
import com.kotcrab.vis.ui.widget.tabbedpane.TabbedPaneAdapter;
import com.vabrant.console.DebugLogger;
import com.vabrant.console.EventListener;
import com.vabrant.console.EventManager;

public abstract class View<T extends Table> {

	private boolean skipTab;
	private boolean showTabbedPane = true;
	private boolean isHidden = true;
	protected final T rootTable;
	protected Table contentTable;
	protected Panel activePanel;
	protected Stage stage;
	private final String name;
	protected GUIConsole console;
	protected ObjectMap<String, Panel> panels;
	protected TabbedPane tabbedPane;
	protected EventManager eventManager;
	protected final DebugLogger logger;

	protected View (String name, T rootTable) {
		this(name, rootTable, null);
	}

	protected View (String name, T rootTable, Panel... panelz) {
		if (name == null || name.isEmpty()) {
			throw new RuntimeException("View name can't be empty or null");
		}

		this.name = name;
		this.rootTable = rootTable;
		contentTable = new Table();
		eventManager = new EventManager();
		panels = new ObjectMap<>();
		tabbedPane = new TabbedPane();
		tabbedPane.addListener(new TabSwitcher());
		logger = new DebugLogger(name + " (View)", DebugLogger.NONE);

		if (panelz != null) {
			for (Panel p : panelz) {
				addPanel(p);
			}
		}

		rootTable.defaults().pad(4);
		if (showTabbedPane) rootTable.add(tabbedPane.getTabsPane()).expandX().fillX().top().row();
		rootTable.add(contentTable).expand().fill();

		setSizePercent(50, 50);
	}

	public void subscribeToEvent (String event, EventListener<?> listener) {
		eventManager.subscribe(event, listener);
	}

	public void unsubscribeFromEvent (String event, EventListener<?> listener) {
		eventManager.unsubscribe(event, listener);
	}

	public void setShowTabbedPane (boolean show) {
		if (showTabbedPane == show) return;

		showTabbedPane = show;

		if (show) {
			rootTable.clearChildren();
			rootTable.add(tabbedPane.getTabsPane()).expandX().fillX().top().row();
			rootTable.add(contentTable).expand().fill();
		} else {
			rootTable.clearChildren();
			rootTable.add(contentTable).expand().fill();
		}
	}

	public DebugLogger getLogger () {
		return logger;
	}

	public String getName () {
		return name;
	}

	public Panel getPanel (String name) {
		return panels.get(name);
	}

	public Panel getActivePanel () {
		return activePanel;
	}

	public void setWidthPercent (float widthPercent) {
		rootTable.setWidth(Gdx.graphics.getWidth() * MathUtils.clamp(widthPercent, 0, 100) / 100f);
	}

	public void setHeightPercent (float heightPercent) {
		rootTable.setHeight(Gdx.graphics.getHeight() * MathUtils.clamp(heightPercent, 0, 100) / 100f);
	}

	public void setWidth (float width) {
		rootTable.setWidth(width);
	}

	public void setHeight (float height) {
		rootTable.setHeight(height);
	}

	public void setSize (float width, float height) {
		rootTable.setSize(width, height);
	}

	public void setSizePercent (float widthPercent, float heightPercent) {
		float w = Gdx.graphics.getWidth() * MathUtils.clamp(widthPercent, 0, 100) / 100f;
		float h = Gdx.graphics.getHeight() * MathUtils.clamp(heightPercent, 0, 100) / 100f;
		rootTable.setSize(w, h);
	}

	public void setX (float x) {
		rootTable.setX(x);
	}

	public void setY (float y) {
		rootTable.setY(y);
	}

	public void setPosition (float x, float y) {
		rootTable.setPosition(x, y);
	}

	public void centerX () {
		if (Gdx.graphics.getWidth() == rootTable.getWidth()) return;
		float x = (Gdx.graphics.getWidth() - rootTable.getWidth()) * 0.5f;
		rootTable.setX(x);
	}

	void setStage (Stage stage) {
		this.stage = stage;
	}

	void setConsole (GUIConsole console) {
		if (this.console != null) {
			throw new RuntimeException("View already added to a console");
		}

		if (activePanel == null) {
			throw new RuntimeException("No active panel set");
		}

		this.console = console;

		contentTable.clearChildren();
		contentTable.add(activePanel.getContentTable()).expand().fill();
	}

	public void setHidden (boolean hide) {
		if (isHidden == hide) return;

		isHidden = hide;

		if (hide) {
			logger.debug("Hide");
			console.getStage().getRoot().removeActor(rootTable);
			console.removeFocusObject(activePanel);
		} else {
			logger.debug("Show");
			console.getStage().addActor(rootTable);
			focus();
		}
	}

	public void addPanel (Panel panel) {
		if (panel == null) {
			throw new IllegalArgumentException("Cant add null panel");
		}

		if (panels.containsKey(panel.getName())) {
			throw new RuntimeException("Panel with name '" + panel.getName() + "' already exists");
		}

		skipTab = true;
		panels.put(panel.getName(), panel);
		panel.setView(this);
		tabbedPane.add(panel);

		if (activePanel != null) {
			skipTab = true;
			tabbedPane.switchTab(activePanel);
		} else {
			activePanel = panel;
		}
	}

	public void setActivePanel (String name) {
		Panel panel = panels.get(name);

		if (panel == null) {
			throw new RuntimeException("No panel found with name '" + name + "'");
		}

		if (isHidden()) {
			activePanel = panel;
			contentTable.clearChildren();
			contentTable.add(activePanel.getContentTable()).expand().fill();
			skipTab = true;
			tabbedPane.switchTab(panel);
		} else {
			tabbedPane.switchTab(panel);
		}
	}

	private void setActivePanel (Panel panel) {
		console.removeFocusObject(activePanel);
		activePanel = panel;
		contentTable.clearChildren();
		contentTable.add(activePanel.getContentTable()).expand().fill();
		focus();
	}

	public boolean isHidden () {
		return isHidden;
	}

	public T getRootTable () {
		return rootTable;
	}

	public GUIConsole getConsole () {
		return console;
	}

	public boolean hasFocus () {
		if (isHidden) return false;
		return console.getFocusObject().equals(activePanel);
	}

	public void focus () {
		if (isHidden()) return;

		if (console.focus(activePanel)) {
			rootTable.toFront();
		}
	}

	public final boolean hit (float x, float y) {
		float vX = rootTable.getX();
		float vY = rootTable.getY();
		float height = rootTable.getHeight();
		float width = rootTable.getWidth();
		return x >= vX && x < vX + width && y >= vY && y < vY + height;
	}

	private class TabSwitcher extends TabbedPaneAdapter {

		@Override
		public void switchedTab (Tab tab) {
			if (skipTab) {
				skipTab = false;
				return;
			}
			setActivePanel((Panel)tab);
		}
	}

}
