
package com.vabrant.console.gui;

import com.badlogic.gdx.*;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.ObjectMap.Values;
import com.vabrant.console.*;
import com.vabrant.console.events.EventManager;
import com.vabrant.console.gui.shortcuts.*;
import com.vabrant.console.gui.views.View;
import com.vabrant.console.log.LogManager;

public interface GUIConsole extends Console {

	Skin getSkin ();

	EventManager getEventManager ();

	/** KeyMap for the GUIConsole. All shortcuts added will have a global scope.
	 * @return DefaultKeyMap */
	DefaultKeyMap getKeyMap ();

	Shortcut addGlobalShortcut (ShortcutCommand command, int... keybind);
	Shortcut addShortcut (KeyboardScope scope, ShortcutCommand command, int... keys);

	View getConsoleView ();

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

	Values<View> getViews ();

}
