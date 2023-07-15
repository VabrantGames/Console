
package com.vabrant.console.commandextension;

public class CommandSettings {

	private boolean clearCommandOnFail = true;
	private boolean useCustomTextFieldInput = false;

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

// public boolean getDebugExecutionStrategy() {
// return debugExecutionStrategy;
// }

}
