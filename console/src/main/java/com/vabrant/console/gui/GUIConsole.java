
package com.vabrant.console.gui;

import com.badlogic.gdx.*;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.ObjectMap.Values;
import com.vabrant.console.*;
import com.vabrant.console.gui.shortcuts.*;
import com.vabrant.console.log.LogManager;

public interface GUIConsole extends Console {

	Skin getSkin ();

	EventManager getEventManager ();

	/** KeyMap for the GUIConsole. All shortcuts added will have a global scope.
	 * @return DefaultKeyMap */
	DefaultKeyMap getKeyMap ();

	KeyMapMultiplexer getKeyMapMultiplexer ();

	Shortcut addShortcut (ShortcutCommand command, int[] keybind);

	View getConsoleView ();

	LogManager getLogManager ();

	ConsoleScope getScope ();

	boolean isScopeActive (ConsoleScope scope);

	InputProcessor getInput ();

	Stage getStage ();

	ShortcutManager getShortcutManager ();

	void resize (int width, int height);

	void draw ();

	boolean focus (FocusObject focusObject);

	FocusObject getFocusObject ();

	void removeFocusObject (FocusObject focusObject);

	void addView (View view);

	View getView (String name);

	Values<View> getViews ();

}
