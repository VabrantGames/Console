
package com.vabrant.console.gui;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.math.MathUtils;
import com.vabrant.console.ConsoleSettings;

public class GUIConsoleConfiguration extends ConsoleSettings {

	int[] toggleConsoleViewKeybind = {Keys.CONTROL_LEFT, Keys.GRAVE};
	int[] closeAllViewsKeybind = {Keys.ESCAPE};
	float consoleViewWidthPercent = 50;
	float consoleViewHeightPercent = 50;
	boolean createConsoleView = true;
	boolean showConsoleView;
	View customConsoleView;

	ViewType consoleViewType = ViewType.MULTI_PANEL_WINDOW;
	String consoleViewName = "ConsoleView";

	public void toggleConsoleViewKeybind (int... keybind) {
		toggleConsoleViewKeybind = keybind;
	}

	public int[] getConsoleViewKeybind () {
		return toggleConsoleViewKeybind;
	}

	public void closeAllViewCommand (int... keybind) {
		closeAllViewsKeybind = keybind;
	}

	public void consoleViewWidthPercent (float percent) {
		consoleViewWidthPercent = MathUtils.clamp(percent, 0, 100);
	}

	public void consoleViewHeightPercent (float percent) {
		consoleViewHeightPercent = MathUtils.clamp(percent, 0, 100);
	}

	public void createConsoleView (boolean createConsoleView) {
		this.createConsoleView = createConsoleView;
	}

	public boolean createConsoleView () {
		return createConsoleView;
	}

	public void consoleViewType (ViewType type) {
		consoleViewType = type;
	}

	public void consoleViewName (String name) {
		consoleViewName = name;
	}

	public void showConsoleView (boolean show) {
		showConsoleView = show;
	}

	public void customConsoleView (View view) {
		customConsoleView = view;
	}

}
