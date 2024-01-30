
package com.vabrant.console.gui.commands;

import com.vabrant.console.ConsoleRuntimeException;
import com.vabrant.console.gui.GUIConsole;
import com.vabrant.console.gui.views.View;
import com.vabrant.console.gui.views.ViewManager;

public class ToggleViewVisibilityCommand implements Runnable {

	// If a view doesn't have focus, executing this command will try to give it focus before removing it.
	private boolean tryToFocusBeforeClosing;
	private boolean onlyOpenIfFocused = true;
	private boolean isViewManager;
	private ViewManager manager;
	private View view;

	public ToggleViewVisibilityCommand (View view) {
		this(view, true);
	}

	public ToggleViewVisibilityCommand (View view, boolean tryToFocusBeforeClosing) {
		if (view == null) {
			throw new ConsoleRuntimeException("View can't be null");
		}

		this.view = view;
		this.tryToFocusBeforeClosing = tryToFocusBeforeClosing;
	}

	public ToggleViewVisibilityCommand (ViewManager manager, View view) {
		this(manager, view, true);
	}

	public ToggleViewVisibilityCommand (ViewManager manager, View view, boolean tryToFocusBeforeClosing) {
		this(view, tryToFocusBeforeClosing);

		if (manager == null) {
			throw new IllegalArgumentException("ViewManager can't be null");
		}

		if (!manager.hasView(view)) {
			throw new ConsoleRuntimeException("View not added to manager");
		}

		this.manager = manager;
	}

	@Override
	public void run () {
		boolean hasManager = manager != null;
		View view = manager == null ? this.view : manager;

		if (view.isHidden()) {
			if (view.show(onlyOpenIfFocused) && hasManager) {
				manager.setActiveView(this.view);
			}
		} else {
			GUIConsole console = view.getGUIConsole();

			if (tryToFocusBeforeClosing && !console.isFocused(view) && console.focus(view)) {
				if (hasManager) manager.setActiveView(this.view);
			} else if (hasManager && !manager.getActiveView().equals(this.view)) {
				manager.setActiveView(this.view);
			} else {
				view.hide();
			}
		}

	}
}
