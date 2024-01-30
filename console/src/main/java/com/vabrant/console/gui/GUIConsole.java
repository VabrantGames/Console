
package com.vabrant.console.gui;

import com.badlogic.gdx.*;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;
import com.vabrant.console.*;
import com.vabrant.console.events.EventManager;
import com.vabrant.console.gui.shortcuts.*;
import com.vabrant.console.gui.views.View;
import com.vabrant.console.log.LogManager;

public interface GUIConsole extends Console {

	Skin getSkin ();

	EventManager getEventManager ();

	/** KeyMap for the GUIConsole.
	 * @return DefaultKeyMap */
	DefaultKeyMap getKeyMap ();

	Shortcut addGlobalShortcut (String ID, Runnable command, int... keybind);

	Shortcut addShortcut (String ID, KeyboardScope scope, Runnable command, int... keys);

	LogManager getLogManager ();

	KeyboardScope getKeyboardScope ();

	boolean isScopeActive (KeyboardScope scope);

	InputProcessor getInput ();

	Stage getStage ();

	GUIConsoleShortcutManager getShortcutManager ();

	void resize (int width, int height);

	void draw ();

	boolean focus (FocusObject focusObject);

	boolean isFocused (FocusObject focusObject);

	FocusObject getFocusObject ();

	void removeFocusObject (FocusObject focusObject);

	void addView (View view);

	View getView (String name);

	Array<View> getViews ();

}
