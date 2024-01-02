package com.vabrant.console.gui.views;

import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.vabrant.console.gui.FocusObject;
import com.vabrant.console.gui.GUIConsole;
import com.vabrant.console.gui.shortcuts.KeyMap;

public interface View<T extends Table, U extends KeyMap> extends FocusObject<U> {
	void setGUIConsole (GUIConsole console);
	GUIConsole getGUIConsole();
	T getRootTable();
	boolean show(boolean focus);
	void hide();
	boolean isHidden();
	boolean isChildView();
	int getZIndex();
	void resize (float oldWidth, float oldHeight, float width, float height);
	boolean hit (float x, float y);
}
