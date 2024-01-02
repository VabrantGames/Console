
package com.vabrant.console.gui.commands;

import com.badlogic.gdx.utils.ObjectMap.Values;
import com.vabrant.console.gui.views.View;
import com.vabrant.console.gui.shortcuts.ShortcutCommand;
import com.vabrant.console.gui.GUIConsole;

public class CloseAllViewsCommand implements ShortcutCommand {

	public GUIConsole console;

	public CloseAllViewsCommand (GUIConsole console) {
		this.console = console;
	}

	@Override
	public void execute () {
		Values<View> views = console.getViews();

		for (View v : views) {
			if (!v.isHidden()) {
				v.hide();
			}
		}
	}
}
