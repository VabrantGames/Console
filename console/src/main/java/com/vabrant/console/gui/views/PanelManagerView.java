
package com.vabrant.console.gui.views;

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
import com.vabrant.console.events.EventListener;
import com.vabrant.console.events.EventManager;
import com.vabrant.console.gui.KeyboardScope;
import com.vabrant.console.gui.shortcuts.KeyMap;

public abstract class PanelManagerView<T extends Table, U extends KeyMap> extends DefaultView<T, U> {

	private final int maxPanels;
	private boolean skipTab;
	protected Table contentTable;
	protected PanelView activePanel;
	protected ObjectMap<String, PanelView> panels;
	protected TabbedPane tabbedPane;
	protected EventManager eventManager;
	private TableSetup rootTableSetup;

//	protected PanelManagerView (String name, Skin skin) {
//		this(name, new Table(), new DefaultKeyMap(), )
//	}

//	protected PanelManagerView (String name, T rootTable, Skin skin, TableSetup rootTableSetup, int maxPanels, PanelView... panelz) {


	protected PanelManagerView (String name, T rootTable, U keyMap, KeyboardScope keyboardScope, Skin skin, TableSetup rootTableSetup, int maxPanels) {
		super(name, rootTable, keyMap, keyboardScope);

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

		this.rootTableSetup = rootTableSetup;

		if (rootTableSetup == null) {
			rootTable.add(tabbedPane.getTable()).growX().row();
			rootTable.add(contentTable).grow();
		} else {
			rootTableSetup.setup(this);
		}
	}

	public void subscribeToEvent (Class event, EventListener<?> listener) {
//		eventManager.subscribe(event, listener);
	}

	public void unsubscribeFromEvent (String event, EventListener<?> listener) {
//		eventManager.unsubscribe(event, listener);
	}

	public DebugLogger getLogger () {
		return logger;
	}

	@Override
	public String getName () {
		return name;
	}

	public PanelView getPanel (String name) {
		return panels.get(name);
	}

	public PanelView getActivePanel () {
		return activePanel;
	}

	public TabbedPane getTabbedPane () {
		return tabbedPane;
	}

	public void addAllPanels (PanelView[] panels) {
		for (PanelView p : panels) {
			addPanel(p);
		}
	}

	public void addPanel (PanelView panel) {
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
		PanelView panel = panels.get(name);

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

	private void setActivePanel (PanelView panel) {
		console.removeFocusObject(activePanel);
		activePanel = panel;
		contentTable.clearChildren();
		contentTable.add(activePanel.getContentTable()).expand().fill();
		focus();
	}

	public Table getContentTable () {
		return contentTable;
	}

	public boolean hasFocus () {
		if (isHidden()) return false;
		return console.getFocusObject().equals(activePanel);
	}

	@Override
	public void unfocus () {

	}

//	public boolean hit (float x, float y) {
//		float vX = rootTable.getX();
//		float vY = rootTable.getY();
//		float height = rootTable.getHeight();
//		float width = rootTable.getWidth();
//		return x >= vX && x < vX + width && y >= vY && y < vY + height;
//	}

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
			setActivePanel((PanelView)tab);
		}
	}

	public interface TableSetup {
		void setup (PanelManagerView view);
	}

}
