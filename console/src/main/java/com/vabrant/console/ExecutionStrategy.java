
package com.vabrant.console;

import com.vabrant.console.log.LogLevel;

public abstract class ExecutionStrategy<T, U> implements Executable<T, Boolean> {

	protected Console console;
//	protected EventManager eventManager;

	public ExecutionStrategy () {
//		eventManager = new EventManager();
	}

	protected final void setConsole (Console console) {
		this.console = console;
	}

//	public void subscribeToEvent (String event, EventListener<U> listener) {
//		eventManager.subscribe(event, listener);
//	}
//
//	public void unsubscribeFromEvent (String event, EventListener<U> listener) {
//		eventManager.subscribe(event, listener);
//	}

	public final void log (String message) {
		if (console != null) {
			console.log(message);
		}
	}

	public final void log (String message, LogLevel level) {
		if (console != null) {
			console.log(message, level);
		}
	}

	public final void log (String tag, String message, LogLevel level) {
		if (console != null) {
			console.log(tag, message, level);
		}
	}
}
