
package com.vabrant.console.gui.commands;

import com.badlogic.gdx.utils.Array;
import com.vabrant.console.gui.views.View;
import com.vabrant.console.gui.GUIConsole;

public class CloseAllViewsCommand implements Runnable {

	public GUIConsole console;

	public CloseAllViewsCommand (GUIConsole console) {
		this.console = console;
	}

	@Override
	public void run () {
		Array<View> views = console.getViews();

		for (View v : views) {
			if (!v.isHidden()) {
				v.hide();
			}
		}
	}
}
