
package com.vabrant.console.commandextension;

public class CommandExtensionSettings {

	private boolean clearCommandOnFail = true;
	private boolean useCustomTextFieldInput = false;
	private boolean debugExecutionStrategy;

	public void setClearCommandOnFail (boolean clear) {
		clearCommandOnFail = clear;
	}

	public boolean getClearCommandOnFail () {
		return clearCommandOnFail;
	}

	public void setUseCustomTextFieldInput (boolean customInput) {
		useCustomTextFieldInput = customInput;
	}

	public boolean getUseCustomTextFieldInput () {
		return useCustomTextFieldInput;
	}

	public void setDebugExecutionStrategy (boolean debug) {
		debugExecutionStrategy = debug;
	}

// public boolean getDebugExecutionStrategy() {
// return debugExecutionStrategy;
// }

}
