
package com.vabrant.console.gui.views;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.vabrant.console.ConsoleRuntimeException;
import com.vabrant.console.DebugLogger;
import com.vabrant.console.Utils;
import com.vabrant.console.gui.GUIConsole;
import com.vabrant.console.gui.KeyboardScope;
import com.vabrant.console.gui.shortcuts.KeyMap;
import space.earlygrey.shapedrawer.ShapeDrawer;

public abstract class DefaultView<T extends Table, U extends KeyMap> implements View<T, U> {

	protected boolean isHidden = true;
	protected boolean isChild;
	protected final String name;
	protected GUIConsole console;
	protected T rootTable;
	protected U keyMap;
	protected KeyboardScope keyboardScope;
	protected DebugLogger logger;

	protected DefaultView (String name, DefaultViewConfiguration<T, U> config) {
		this.name = name;
		this.rootTable = config.rootTable;
		this.keyMap = config.keyMap;
		this.keyboardScope = config.keyboardScope;

		if (config.createTitleBar) {
			createTitleBar(config.shapeDrawer, config.skin);
		}

		if (config.x != -1) {
			setX(config.x);
		}

		if (config.y != -1) {
			setY(config.y);
		}

		if (config.width != -1) {
			setWidth(config.width);
		} else if (config.widthPercent != -1) {
			setWidthPercent(config.widthPercent);
		}

		if (config.height != -1) {
			setHeight(config.height);
		} else if (config.heightPercent != -1) {
			setHeightPercent(config.heightPercent);
		}

		if (config.centerX) {
			centerX();
		}
	}

	protected DefaultView (String name, T rootTable, U keyMap, KeyboardScope keyboardScope) {
		this.name = name;
		this.rootTable = rootTable;
		this.keyMap = keyMap;
		this.keyboardScope = keyboardScope;
		logger = new DebugLogger(name + " (View)", DebugLogger.NONE);
	}

	@Override
	public void focus () {
		rootTable.toFront();
	}

	@Override
	public void unfocus () {

	}

	@Override
	public KeyboardScope getKeyboardScope () {
		return keyboardScope;
	}

	@Override
	public U getKeyMap () {
		return keyMap;
	}

	@Override
	public boolean lockFocus () {
		return false;
	}

	public String getName () {
		return name;
	}
	@Override
	public int getZIndex () {
		return rootTable.getZIndex();
	}

	protected void createTitleBar(ShapeDrawer shapeDrawer, Skin skin) {
		createTitleBar(shapeDrawer, skin, Color.LIGHT_GRAY, Color.DARK_GRAY, Color.WHITE);
	}

	protected void createTitleBar(ShapeDrawer shapeDrawer, Skin skin, Color backgroundColor, Color barColor, Color fontColor) {
		Table barTable = new Table();
		barTable.setBackground(Utils.createFilledRectangleDrawable(shapeDrawer, barColor));
		LabelStyle style = new LabelStyle(skin.get(LabelStyle.class));
		barTable.add(new Label(name, style));
		rootTable.add(barTable).growX().row();
	}

	@Override
	public void resize (float oldWidth, float oldHeight, float width, float height) {
		System.out.println("Resie");
	}

	@Override
	public boolean hit (float x, float y) {
		Actor a = console.getStage().hit(x, y, false);

		if (a == null) return false;

		if (a.equals(rootTable)) return false;

		boolean contains = rootTable.getChildren().contains(getTopParent(a), false);

		return contains;
	}

	private Actor getTopParent(Actor a) {

		Actor parent = a.getParent();

		if (parent == null || parent.equals(rootTable)) return null;

		Actor p = parent.getParent();
		while (p != null) {
			if (p.equals(rootTable)) {
				p = null;
			} else {
				parent = p;
				p = parent.getParent();
			}
		}

		return parent;
	}

	@Override
	public void setGUIConsole (GUIConsole console) {
		if (this.console != null) {
			throw new ConsoleRuntimeException("View already added to a console");
		}

		this.console = console;
	}

	@Override
	public GUIConsole getGUIConsole () {
		return console;
	}

	@Override
	public T getRootTable () {
		return rootTable;
	}

	@Override
	public boolean show (boolean focus) {
		if (focus && !console.focus(this)) return false;
		isHidden = false;
		console.getStage().addActor(rootTable);
		return true;
	}

	@Override
	public void hide () {
		isHidden = true;
		console.getStage().getRoot().removeActor(rootTable);
		console.removeFocusObject(this);
	}

	public boolean isHidden () {
		return isHidden;
	}

	@Override
	public boolean isChildView () {
		return false;
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
		setY(Gdx.graphics.getHeight() - rootTable.getHeight());
	}

	public void centerX () {
		if (Gdx.graphics.getWidth() == rootTable.getWidth()) return;
		rootTable.setX((Gdx.graphics.getWidth() - rootTable.getWidth()) * 0.5f);
	}
}
