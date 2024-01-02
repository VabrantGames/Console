
package com.vabrant.console.gui.commands;

import com.vabrant.console.ConsoleRuntimeException;
import com.vabrant.console.gui.GUIConsole;
import com.vabrant.console.gui.views.View;
import com.vabrant.console.gui.shortcuts.ShortcutCommand;

public class ToggleViewVisibilityCommand implements ShortcutCommand {

	// If a view doesn't have focus, executing this command will give it focus before removing it.
	private boolean tryToFocusBeforeClosing;
	private boolean onlyOpenIfFocused = true;
	private View view;

	public ToggleViewVisibilityCommand (View view) {
		this(view, false);
	}

	public ToggleViewVisibilityCommand (View view, boolean tryToFocusBeforeClosing) {
		if (view == null) {
			throw new ConsoleRuntimeException("View can't be null");
		}

		this.view = view;
		this.tryToFocusBeforeClosing = tryToFocusBeforeClosing;
	}

	@Override
	public void execute () {
		if (view.isHidden()) {
			view.show(onlyOpenIfFocused);
		} else {
			GUIConsole console = view.getGUIConsole();
			if (tryToFocusBeforeClosing && !console.isFocused(view)) {
				view.getGUIConsole().focus(view);
			} else {
				view.hide();
			}
		}

	}
}
