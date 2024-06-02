
package com.vabrant.console.CommandEngine;

import com.vabrant.console.Utils;

public class CommandEngineExecutionResult {

	private boolean executionResult;
	private String errorString;
	private Object result;

	public void setExecutionStatus (boolean result) {
		executionResult = result;
	}

	public boolean getExecutionStatus () {
		return executionResult;
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
		executionResult = false;
		errorString = null;
		result = null;
	}
}
