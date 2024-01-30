
package com.vabrant.console.gui;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.math.MathUtils;

public class DefaultGUIConsoleConfiguration {

	protected int[] toggleConsoleViewKeybind = {Keys.CONTROL_LEFT, Keys.GRAVE};
	protected int[] closeAllViewsKeybind = {Keys.ESCAPE};
	protected int[] toggleCommandLineKeybind = {Keys.GRAVE};
	protected float consoleViewWidthPercent;
	protected float consoleViewHeightPercent;
	protected boolean createConsoleView = true;
	protected boolean showConsoleView;

	protected String consoleViewName = "ConsoleView";

	public void toggleConsoleViewKeybind (int... keybind) {
		toggleConsoleViewKeybind = keybind;
	}

	public void closeAllViewsKeybind (int... keybind) {
		closeAllViewsKeybind = keybind;
	}

	public void toggleCommandLineKeybind (int... keybind) {
		toggleCommandLineKeybind = keybind;
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

// public boolean createConsoleView () {
// return createConsoleView;
// }

	public void consoleViewName (String name) {
		consoleViewName = name;
	}

	public void showConsoleView (boolean show) {
		showConsoleView = show;
	}

}
