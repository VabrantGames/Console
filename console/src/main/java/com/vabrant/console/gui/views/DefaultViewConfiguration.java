package com.vabrant.console.gui.views;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.vabrant.console.gui.KeyboardScope;
import com.vabrant.console.gui.shortcuts.KeyMap;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class DefaultViewConfiguration {

	public boolean createTitleBar;
	public boolean centerX;
	public boolean positionPercent;
	public boolean sizePercent;
	public float x = -1;
	public float y = -1;
	public float xPercent = -1;
	public float yPercent = -1;
	public float width = -1;
	public float height = -1;
	public float widthPercent;
	public float heightPercent;
	public KeyboardScope keyboardScope;
	public Skin skin;
	public ShapeDrawer shapeDrawer;

	public DefaultViewConfiguration() {
	}

	public DefaultViewConfiguration (Skin skin, ShapeDrawer shapeDrawer) {
		this.skin = skin;
		this.shapeDrawer = shapeDrawer;
	}

	public DefaultViewConfiguration createTitleBar (boolean createTitleBar) {
		this.createTitleBar = createTitleBar;
		return this;
	}

//	public DefaultViewConfiguration<T, U> setKeyboardScope (KeyboardScope keyboardScope) {
//		this.keyboardScope = keyboardScope;
//		return this;
//	}

	public DefaultViewConfiguration centerX (boolean centerX) {
		this.centerX = centerX;
		return this;
	}

	public DefaultViewConfiguration setWidth (float width) {
		this.width = width;
		widthPercent = -1;
		return this;
	}

	public DefaultViewConfiguration setWidthPercent (float widthPercent) {
		this.widthPercent = widthPercent;
		width = -1;
		return this;
	}

	public DefaultViewConfiguration setHeight (float height) {
		this.height = height;
		heightPercent = -1;
		return this;
	}

	public DefaultViewConfiguration setHeightPercent (float heightPercent) {
		this.heightPercent = heightPercent;
		height = -1;
		return this;
	}
}
