
package com.vabrant.console.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.vabrant.console.DebugLogger;

public abstract class View<T extends Table> {

	private boolean isHidden = true;
	protected final T rootTable;
	protected Table contentTable;
	protected Panel activePanel;
	protected Stage stage;
	private final String name;
	protected GUIConsole console;
	private int visibilityKeybindPacked = -1;
	private DebugLogger logger;

	protected View (String name, T rootTable, Panel panel) {
		if (name == null || name.isEmpty()) {
			throw new RuntimeException("View name can't be empty or null");
		}

		this.name = name;
		this.rootTable = rootTable;
		this.activePanel = panel;
		logger = new DebugLogger(name + " (View)", DebugLogger.DEBUG);
		contentTable = new Table();

		if (panel != null) {
			panel.setView(this);
			contentTable.add(panel.getContentTable()).expand().fill();
		}

		rootTable.defaults().pad(4);
		rootTable.add(contentTable).expand().fill();
	}

	public void setVisibilityKeybindPacked(int packed) {
		visibilityKeybindPacked = packed;
	}

	public int getVisibilityKeybindPacked() {
		return visibilityKeybindPacked;
	}

	public DebugLogger getLogger () {
		return logger;
	}

	public String getName () {
		return name;
	}

	public Panel getPanel() {
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

	public void centerX() {
		if (Gdx.graphics.getWidth() == rootTable.getWidth()) return;
		float x = (Gdx.graphics.getWidth() - rootTable.getWidth()) * 0.5f;
		rootTable.setX(x);
	}

	void setStage (Stage stage) {
		this.stage = stage;
	}

	void setConsole (GUIConsole console) {
		this.console = console;
	}

	public void setHidden (boolean hidden) {
		if (isHidden == hidden) return;

		isHidden = hidden;

		if (isHidden) {
			console.getStage().getRoot().removeActor(rootTable);
			unfocus();
			logger.debug("Hide");
		} else {
			console.getStage().addActor(rootTable);
			focus();
			logger.debug("Show");
		}
	}

	public boolean isHidden () {
		return isHidden;
	}

	public T getRootTable () {
		return rootTable;
	}

	public GUIConsole getConsole() {
		return console;
	}

	public boolean hasFocus () {
		if (isHidden) return false;
		View v = console.getFocusedView();
		return v != null && v.equals(this);
	}

	void unfocus() {
		if (console.resetFocus(this)) {
			activePanel.unfocus();
		}
		logger.info("Unfocus");
	}

	public void focus () {
		logger.info("Focus");

		if (console.focusView(this)) {
			activePanel.focus();
		}
	}

	public final boolean hit (float x, float y) {
		float vX = rootTable.getX();
		float vY = rootTable.getY();
		float height = rootTable.getHeight();
		float width = rootTable.getWidth();
		return x >= vX && x < vX + width && y >= vY && y < vY + height;
	}

}
