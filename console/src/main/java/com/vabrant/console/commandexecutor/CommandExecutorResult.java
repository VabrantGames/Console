
package com.vabrant.console.commandexecutor;

import com.vabrant.console.Utils;

public class CommandExecutorResult {

	private boolean executionStatus;
	private String errorString;
	private Object result;

	public void setExecutionStatus (boolean result) {
		executionStatus = result;
	}

	public boolean getExecutionStatus () {
		return executionStatus;
	}

	public void setErrorString (String string) {
		errorString = string;
	}

	public void setErrorString (Throwable throwable) {
		errorString = Utils.exceptionToString(throwable);
	}

	public String getErrorString () {
		return errorString;
	}

	public void setResult (Object result) {
		this.result = result;
	}

	public Object getResult () {
		return result;
	}

	public void clear () {
		executionStatus = false;
		errorString = null;
		result = null;
	}
}
