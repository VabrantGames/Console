
package com.vabrant.console.gui;

import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.ObjectMap;
import com.kotcrab.vis.ui.widget.tabbedpane.Tab;
import com.kotcrab.vis.ui.widget.tabbedpane.TabbedPane;
import com.kotcrab.vis.ui.widget.tabbedpane.TabbedPaneAdapter;

public abstract class MultiPanelView<T extends Table> extends View<T> {

	private boolean addedToConsole;
	private ObjectMap<String, Panel> panels;
	private TabbedPane tabbedPane;

	protected MultiPanelView (String name, T viewTable) {
		super(name, viewTable, null);

		panels = new ObjectMap<>();
		tabbedPane = new TabbedPane();
		tabbedPane.addListener(new TabSwitcher());

        viewTable.clearChildren();
        viewTable.add(tabbedPane.getTabsPane()).expandX().fillX().top().row();
        viewTable.add(contentTable).expand().fill();
	}

	@Override
	void setConsole(GUIConsole console) {
		super.setConsole(console);
		initActivePanel();
		addedToConsole = true;
	}

	public void addPanel (Panel panel) {
		if (panels.containsKey(panel.getName())) {
			throw new RuntimeException("Panel with name already exists");
		}

		panels.put(panel.getName(), panel);
		panel.setView(this);
		tabbedPane.add(panel);

		if (activePanel == null) {
			activePanel = panel;
		}
	}

	public void setActivePanel (String name) {
		Panel p = panels.get(name);

		if (p == null) {
			throw new RuntimeException("No panel found with name '" + name + "'");
		}

		setActivePanel(p);
	}

	private void setActivePanel (Panel panel) {
		activePanel.unfocus();
		activePanel = panel;
		contentTable.clearChildren();
		contentTable.add(activePanel.getContentTable()).expand().fill();
        tabbedPane.switchTab(panel);
		activePanel.focus();
	}

	private void initActivePanel() {
		contentTable.clearChildren();
		contentTable.add(activePanel.getContentTable()).expand().fill();
        tabbedPane.switchTab(activePanel);
	}

	public Panel getPanel(String panel) {
		return panels.get(panel);
	}

	private class TabSwitcher extends TabbedPaneAdapter {
		@Override
		public void switchedTab (Tab tab) {
			if (addedToConsole) setActivePanel((Panel)tab);
		}
	}
}
