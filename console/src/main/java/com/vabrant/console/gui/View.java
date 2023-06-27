
package com.vabrant.console.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

public abstract class View<T extends Table> {

	private boolean isHidden = false;
	protected final T rootTable;
	protected Table contentTable;
	protected Panel activePanel;
	protected Stage stage;
	private final String name;

	protected View (String name, T rootTable, Panel panel) {
		this.name = name;
		this.rootTable = rootTable;
		this.activePanel = panel;
		contentTable = new Table();

		if (panel != null) {
			contentTable.add(panel.getContentTable()).expand().fill();
		}

		rootTable.defaults().pad(4);
		rootTable.add(contentTable).expand().fill();

		rootTable.setTouchable(Touchable.disabled);
	}

	public String getName () {
		return name;
	}

	public void setWidthPercent (float widthPercent) {
		rootTable.setWidth(Gdx.graphics.getWidth() * MathUtils.clamp(widthPercent, 0, 100) / 100f);
	}

	public void setHeightPercent (float heightPercent) {
		rootTable.setHeight(Gdx.graphics.getHeight() * MathUtils.clamp(heightPercent, 0, 100) / 100f);
	}

	public void setSize (float width, float height) {
		rootTable.setSize(width, height);
	}

	public void setSizePercent (float widthPercent, float heightPercent) {
		float w = Gdx.graphics.getWidth() * MathUtils.clamp(widthPercent, 0, 100) / 100f;
		float h = Gdx.graphics.getHeight() * MathUtils.clamp(heightPercent, 0, 100) / 100f;
		rootTable.setSize(w, h);
	}

	public void setPosition (float x, float y) {
		rootTable.setPosition(x, y);
	}

	void setStage (Stage stage) {
		this.stage = stage;
	}

	public void setHidden (boolean hidden) {
		isHidden = hidden;

		if (isHidden) {
			stage.getRoot().removeActor(rootTable);
		} else {
			stage.addActor(rootTable);
		}
	}

	public boolean isHidden () {
		return isHidden;
	}

	public T getRootTable () {
		return rootTable;
	}

	public void setActive(boolean active) {
		if (isHidden) return;

		if (active) {
			rootTable.setTouchable(Touchable.disabled);
		} else {
			rootTable.setTouchable(Touchable.enabled);
		}
	}

}
