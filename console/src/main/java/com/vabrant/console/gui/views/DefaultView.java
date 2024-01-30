
package com.vabrant.console.gui.views;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.vabrant.console.ConsoleRuntimeException;
import com.vabrant.console.DebugLogger;
import com.vabrant.console.KeyboardScope;
import com.vabrant.console.Utils;
import com.vabrant.console.gui.GUIConsole;
import com.vabrant.console.gui.shortcuts.KeyMap;

public abstract class DefaultView implements View {

	protected boolean isDirty;
	protected boolean keepInScreenBounds = true;
	protected boolean isHidden = true;
	protected final String name;
	protected GUIConsole console;
	protected Table rootTable;
	protected Table contentTable;
	protected Table titleBarTable;
	protected KeyMap keyMap;
	protected KeyboardScope keyboardScope;
	protected DebugLogger logger;
	protected ViewManager viewManager;
	protected TitleBar titleBar;

	protected DefaultView (String name) {
		this(name, null, (Table)null);
	}

	protected DefaultView (String name, Table rootTable) {
		this(name, rootTable, new Table());
	}

	protected DefaultView (String name, Table rootTable, Table contentTable) {
		if (name == null || name.isEmpty()) {
			throw new IllegalArgumentException("View name can't be empty or null");
		}

		this.name = name;
		this.rootTable = rootTable;
		this.contentTable = contentTable;

		if (rootTable != null) {
			if (contentTable != null) {
				rootTable.add(contentTable).grow();
			}

			setSizePercent(20, 50);
		} else {
			isDirty = true;
		}

		logger = new DebugLogger(name + " (View)", DebugLogger.NONE);
	}

	public DebugLogger getLogger () {
		return logger;
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
	public KeyMap getKeyMap () {
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

	public void keepInScreenBounds (boolean keepInScreenBounds) {
		this.keepInScreenBounds = keepInScreenBounds;
	}

	@Override
	public void setViewManager (ViewManager manager) {
		viewManager = manager;
	}

	public void setTitleBar (TitleBar titleBar) {
		setTitleBar(name, titleBar);
	}

	public void setTitleBar (String title, TitleBar titleBar) {
		if (titleBar == null) {
			this.titleBar = null;
		} else {
			this.titleBar = titleBar;
			titleBar.setTitle(title);
		}

		isDirty = true;
	}

	private void refreshRootTable () {
		isDirty = false;

		if (contentTable == null) {
			contentTable = new Table();
		}

		rootTable.clearChildren();
		rootTable.clip(true);

		if (titleBar != null) {
			rootTable.add(titleBar.getTable()).growX().row();
		}

		rootTable.add(contentTable).grow();
		rootTable.invalidateHierarchy();
	}

	@Override
	public void resize (float oldWidth, float oldHeight, float width, float height) {
		isDirty = true;

		float xPercent = rootTable.getX() / oldWidth * 100;
		float yPercent = rootTable.getY() / oldHeight * 100;
		float widthPercent = rootTable.getWidth() / oldWidth * 100;
		float heightPercent = rootTable.getHeight() / oldHeight * 100;

		setSizePercent(widthPercent, heightPercent);
		setPositionPercent(xPercent, yPercent);
	}

	@Override
	public boolean hit (float x, float y) {
		Actor a = console.getStage().hit(x, y, false);

		if (a == null) return false;

		if (a.equals(rootTable)) return false;

		boolean contains = rootTable.getChildren().contains(getTopParent(a), false);

		return contains;
	}

	private Actor getTopParent (Actor a) {

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
	public Table getRootTable () {
		return rootTable;
	}

	@Override
	public boolean show (boolean focus) {
		if (focus && !console.focus(this)) return false;
		if (isDirty) refreshRootTable();
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
		return viewManager != null;
	}

	public void setWidthPercent (float widthPercent) {
// rootTable.setWidth(Gdx.graphics.getWidth() * MathUtils.clamp(widthPercent, 0, 100) / 100f);
		rootTable.setWidth(Gdx.graphics.getWidth() * MathUtils.map(0, 100, 0, 1, widthPercent));
		keepInScreenBounds();
	}

	public void setHeightPercent (float heightPercent) {
		rootTable.setHeight(Gdx.graphics.getHeight() * MathUtils.clamp(heightPercent, 0, 100) / 100f);
		keepInScreenBounds();
	}

	public void setWidth (float width) {
		rootTable.setWidth(width);
		keepInScreenBounds();
	}

	public void setHeight (float height) {
		rootTable.setHeight(height);
		keepInScreenBounds();
	}

	public void setSize (float width, float height) {
		rootTable.setSize(width, height);
		keepInScreenBounds();
	}

	public void setSizePercent (float widthPercent, float heightPercent) {
		float w = Gdx.graphics.getWidth() * MathUtils.map(0, 100, 0, 1, widthPercent);
		float h = Gdx.graphics.getHeight() * MathUtils.clamp(heightPercent, 0, 100) / 100f;
		rootTable.setSize(w, h);
		keepInScreenBounds();
	}

	public void translateX (float amt) {
		rootTable.setX(rootTable.getX() + amt);
	}

	public void translateY (float amt) {
		rootTable.setY(rootTable.getY() + amt);
	}

	public void translate (float xAmt, float yAmt) {
		rootTable.setX(rootTable.getX() + xAmt);
		rootTable.setY(rootTable.getY() + yAmt);
	}

	public void setX (float x) {
		rootTable.setX(x);
	}

	public void setY (float y) {
		rootTable.setY(y);
	}

	public void setXPercent (float percent) {
		float x = Gdx.graphics.getWidth() * MathUtils.map(0, 100, 0, 1, percent);

		if (x + rootTable.getWidth() >= Gdx.graphics.getWidth()) {
			rootTable.setX(Gdx.graphics.getWidth() - rootTable.getWidth());
		} else {
			rootTable.setX(x);
		}
	}

	public void setYPercent (float percent) {
		float y = Gdx.graphics.getHeight() * MathUtils.map(0, 100, 0, 1, percent);

		if (y + rootTable.getHeight() >= Gdx.graphics.getHeight()) {
			rootTable.setY(Gdx.graphics.getHeight() - rootTable.getHeight());
		} else {
			rootTable.setY(y);
		}
	}

	public void setPosition (float x, float y) {
		rootTable.setPosition(x, y);
	}

	public void setPositionPercent (float xPercent, float yPercent) {
		float x = Gdx.graphics.getWidth() * MathUtils.map(0, 100, 0, 1, xPercent);

		if (x + rootTable.getWidth() > Gdx.graphics.getWidth()) {
			x = Gdx.graphics.getWidth() - rootTable.getWidth();
		}

		float y = Gdx.graphics.getHeight() * MathUtils.map(0, 100, 0, 1, yPercent);

		if (y + rootTable.getHeight() > Gdx.graphics.getHeight()) {
			y = Gdx.graphics.getHeight() - rootTable.getHeight();
		}

		rootTable.setPosition(x, y);
	}

	public void setPosition (int position) {
		if ((Utils.LEFT & position) != 0 && (Utils.RIGHT & position) != 0) {
			setX((Gdx.graphics.getWidth() - rootTable.getWidth()) * 0.5f);
		} else if ((Utils.LEFT & position) != 0) {
			setXPercent(0);
		} else if ((Utils.RIGHT & position) != 0) {
			System.out.println("Right");
			setXPercent(100);
		}

		if ((Utils.TOP & position) != 0 && (Utils.BOTTOM & position) != 0) {
			setY((Gdx.graphics.getHeight() - rootTable.getHeight()) * 0.5f);
		} else if ((Utils.TOP & position) != 0) {
			setYPercent(100);
		} else if ((Utils.BOTTOM & position) != 0) {
			setYPercent(0);
		}
	}

	public void moveToTop () {
		if (rootTable.getHeight() >= Gdx.graphics.getHeight()) return;
		setYPercent(100);
// setY(Gdx.graphics.getHeight() - rootTable.getHeight());
	}

	public void centerX () {
		if (Gdx.graphics.getWidth() == rootTable.getWidth()) return;
		rootTable.setX((Gdx.graphics.getWidth() - rootTable.getWidth()) * 0.5f);
	}

	protected void keepInScreenBounds () {
		if (!keepInScreenBounds) return;

		if (rootTable.getX() < 0) {
			rootTable.setX(0);
		} else if (rootTable.getX() + rootTable.getWidth() > Gdx.graphics.getWidth()) {
			rootTable.setX(Gdx.graphics.getWidth() - rootTable.getWidth());
		}

		if (rootTable.getY() < 0) {
			rootTable.setY(0);
		} else if (rootTable.getY() + rootTable.getHeight() > Gdx.graphics.getHeight()) {
			rootTable.setY(Gdx.graphics.getHeight() - rootTable.getHeight());
		}
	}

}
