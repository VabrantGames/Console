
package com.vabrant.console.commandextension;

import com.badlogic.gdx.Input.Keys;
import com.vabrant.console.gui.GUIConsoleConfiguration;

public class CommandConsoleConfiguration extends GUIConsoleConfiguration {

	boolean centerCommandLine = true;
	boolean repositionConsoleViewWithCommandLine = true;
	float commandLineWidthPercent = 80;
	int[] commandLineKeybind = {Keys.GRAVE};

	CommandSettings commandSettings;

	public void commandLineKeybind (int... keybind) {
		commandLineKeybind = keybind;
	}

	public int[] getCommandLineKeybind() {
		return commandLineKeybind;
	}

	public void setRepositionConsoleViewWithCommandLine (boolean reposition) {
		repositionConsoleViewWithCommandLine = reposition;
	}

	public void centerCommandLine (boolean center) {
		centerCommandLine = center;
	}

	public void commandLineWidthPercent (float percent) {
		commandLineWidthPercent = percent;
	}

	public void setCommandExtensionSettings (CommandSettings settings) {
		commandSettings = settings;
	}

	public CommandSettings getCommandExtensionSettings () {
		return commandSettings;
	}

}
