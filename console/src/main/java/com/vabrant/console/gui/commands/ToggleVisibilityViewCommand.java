
package com.vabrant.console.gui.commands;

import com.vabrant.console.ConsoleCommand;
import com.vabrant.console.gui.View;

public class ToggleVisibilityViewCommand implements ConsoleCommand {

	//If a view doesn't have focus, executing this command will give it focus before removing it.
	private boolean focusBeforeClosing;
	private View<?> view;

	public ToggleVisibilityViewCommand(View<?> view) {
		this(view, false);
	}

	public ToggleVisibilityViewCommand(View<?> view, boolean focusBeforeClosing) {
		if (view == null) {
			throw new RuntimeException("View can't be null");
		}

		this.view = view;
		this.focusBeforeClosing = focusBeforeClosing;
	}

	private void toggle() {
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
