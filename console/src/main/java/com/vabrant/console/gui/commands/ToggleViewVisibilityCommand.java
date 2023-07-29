
package com.vabrant.console.gui.commands;

import com.vabrant.console.gui.View;
import com.vabrant.console.gui.shortcuts.ShortcutCommand;

public class ToggleViewVisibilityCommand implements ShortcutCommand {

	// If a view doesn't have focus, executing this command will give it focus before removing it.
	private boolean focusBeforeClosing;
	private View view;

	public ToggleViewVisibilityCommand (View view) {
		this(view, false);
	}

	public ToggleViewVisibilityCommand (View view, boolean focusBeforeClosing) {
		if (view == null) {
			throw new RuntimeException("View can't be null");
		}

		this.view = view;
		this.focusBeforeClosing = focusBeforeClosing;
	}

	private void toggle () {
		view.setHidden(!view.isHidden());
	}

	@Override
	public void execute () {
		if (view.isHidden()) {
			toggle();
		} else {
			if (focusBeforeClosing) {
				if (!view.hasFocus()) {
					view.focus();
				} else {
					toggle();
				}
			} else {
				toggle();
			}
		}

	}
}
