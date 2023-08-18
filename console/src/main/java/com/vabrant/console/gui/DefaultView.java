
package com.vabrant.console.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.ObjectMap;
import com.kotcrab.vis.ui.Sizes;
import com.kotcrab.vis.ui.widget.tabbedpane.Tab;
import com.kotcrab.vis.ui.widget.tabbedpane.TabbedPane;
import com.kotcrab.vis.ui.widget.tabbedpane.TabbedPane.TabbedPaneStyle;
import com.kotcrab.vis.ui.widget.tabbedpane.TabbedPaneAdapter;
import com.vabrant.console.ConsoleRuntimeException;
import com.vabrant.console.DebugLogger;
import com.vabrant.console.EventListener;
import com.vabrant.console.EventManager;

public abstract class DefaultView<T extends Table> extends View {

	private final int maxPanels;
	private boolean skipTab;
	protected final T rootTable;
	protected Table contentTable;
	protected Panel activePanel;
	protected Stage stage;
	protected ObjectMap<String, Panel> panels;
	protected TabbedPane tabbedPane;
	protected EventManager eventManager;
	protected final DebugLogger logger;
	private TableSetup rootTableSetup;

// protected DefaultView (String name, T rootTable) {
// this(name, rootTable, null, null, null);
// }

	protected DefaultView (String name, T rootTable, Skin skin, Panel... panelz) {
		this(name, rootTable, skin, null, 10, panelz);
	}

	protected DefaultView (String name, T rootTable, Skin skin, TableSetup rootTableSetup, int maxPanels, Panel... panelz) {
		super(name);

		if (name == null || name.isEmpty()) {
			throw new ConsoleRuntimeException("View name can't be empty or null");
		}

		this.maxPanels = maxPanels;
		this.rootTable = rootTable;
		contentTable = new Table();
		eventManager = new EventManager();
		panels = new ObjectMap<>();
		tabbedPane = new TabbedPane(skin.get(TabbedPaneStyle.class), skin.get(Sizes.class));
		tabbedPane.getTabsPane().setDraggable(null);
		tabbedPane.addListener(new TabSwitcher());
		logger = new DebugLogger(name + " (View)", DebugLogger.NONE);

		if (panelz != null) {
			for (Panel p : panelz) {
				addPanel(p);
			}
		}

		this.rootTableSetup = rootTableSetup;

		if (rootTableSetup == null) {
			rootTable.add(tabbedPane.getTable()).growX().row();
			rootTable.add(contentTable).grow();
		} else {
			rootTableSetup.setup(this);
		}
	}

	public void subscribeToEvent (String event, EventListener<?> listener) {
		eventManager.subscribe(event, listener);
	}

	public void unsubscribeFromEvent (String event, EventListener<?> listener) {
		eventManager.unsubscribe(event, listener);
	}

	public DebugLogger getLogger () {
		return logger;
	}

	@Override
	public String getName () {
		return name;
	}

	public Panel getPanel (String name) {
		return panels.get(name);
	}

	public Panel getActivePanel () {
		return activePanel;
	}

	public TabbedPane getTabbedPane () {
		return tabbedPane;
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

	public void moveToTop () {
		if (rootTable.getHeight() >= Gdx.graphics.getHeight()) return;
		float y = Gdx.graphics.getHeight() - rootTable.getHeight();
		setY(y);
	}

	public void centerX () {
		if (Gdx.graphics.getWidth() == rootTable.getWidth()) return;
		float x = (Gdx.graphics.getWidth() - rootTable.getWidth()) * 0.5f;
		rootTable.setX(x);
	}

	@Override
	public void setConsole (GUIConsole console) {
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

	@Override
	public void setHidden (boolean hide) {
		if (isHidden() == hide) return;

		super.setHidden(hide);

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
		if (panels.size == maxPanels) {
			throw new ConsoleRuntimeException("Panel limit reached");
		}

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

	public T getRootTable () {
		return rootTable;
	}

	public Table getContentTable () {
		return contentTable;
	}

	public GUIConsole getConsole () {
		return console;
	}

	public boolean hasFocus () {
		if (isHidden()) return false;
		return console.getFocusObject().equals(activePanel);
	}

	@Override
	public void focus () {
		if (isHidden()) return;

		if (console.focus(activePanel)) {
			rootTable.toFront();
		}
	}

	@Override
	public int getZIndex () {
		return rootTable.getZIndex();
	}

	public boolean hit (float x, float y) {
		float vX = rootTable.getX();
		float vY = rootTable.getY();
		float height = rootTable.getHeight();
		float width = rootTable.getWidth();
		return x >= vX && x < vX + width && y >= vY && y < vY + height;
	}

	public void resize (float oldWidth, float oldHeight, float newWidth, float newHeight) {
		float wPct = getRootTable().getWidth() / oldWidth * 100;
		float hPct = getRootTable().getHeight() / oldHeight * 100;

		setSizePercent(wPct, hPct);
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

	public interface TableSetup {
		void setup (DefaultView<?> view);
	}

}
