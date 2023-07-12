
package com.vabrant.console;

public abstract class ExecutionStrategy<U extends ExecutionData> implements Executable<Object, Boolean> {

	protected Console console;
	protected EventManager eventManager;
	protected U data;

	protected ExecutionStrategy () {
		eventManager = new EventManager();
	}

	protected final void setConsole (Console console) {
		this.console = console;
	}

	public void subscribeToEvent (String event, EventListener<?> listener) {
		eventManager.subscribe(event, listener);
	}

	public void unsubscribeFromEvent (String event, EventListener<?> listener) {
		eventManager.subscribe(event, listener);
	}

	protected void fireEvent (String event, Object data) {
		eventManager.fire(event, data);
	}

	public void init (U data) {
		this.data = data;
		data.setExecutionStrategy(this);
	}

// public void setData(U data) {
// data.s
// }

// public final void log (String message) {
// if (console != null) {
// console.log(message);
// }
// }
//
// public final void log (String message, LogLevel level) {
// if (console != null) {
// console.log(message, level);
// }
// }
//
// public final void log (String tag, String message, LogLevel level) {
// if (console != null) {
// console.log(tag, message, level);
// }
// }
}
