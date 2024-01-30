
package com.vabrant.console.gui.views;

import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.vabrant.console.gui.FocusObject;
import com.vabrant.console.gui.GUIConsole;

public interface View extends FocusObject {
	void setGUIConsole (GUIConsole console);

	GUIConsole getGUIConsole ();

	Table getRootTable ();

	boolean show (boolean focus);

	void hide ();

	boolean isHidden ();

	boolean isChildView ();

	int getZIndex ();

	void resize (float oldWidth, float oldHeight, float width, float height);

	boolean hit (float x, float y);

	void setViewManager (ViewManager manager);

}
