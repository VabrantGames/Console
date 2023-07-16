
package com.vabrant.console;

public abstract class ConsoleStrategy<U extends ConsoleData> implements Executable<Object, Boolean> {

	protected Console console;
	protected EventManager eventManager;
	protected U data;

	protected ConsoleStrategy () {
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
		data.setConsoleStrategy(this);
	}

}
