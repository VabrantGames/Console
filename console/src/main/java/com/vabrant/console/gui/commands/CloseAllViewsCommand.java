package com.vabrant.console.gui.commands;

import com.badlogic.gdx.utils.ObjectMap.Values;
import com.vabrant.console.ConsoleCommand;
import com.vabrant.console.gui.GUIConsole;
import com.vabrant.console.gui.View;

public class CloseAllViewsCommand implements ConsoleCommand {

	public GUIConsole console;

		public CloseAllViewsCommand (GUIConsole console) {
			this.console = console;
		}

		@Override
		public void execute () {
			Values<View<?>> views = console.getViews();

			for (View<?> v : views) {
				v.setHidden(true);
			}
		}
}
