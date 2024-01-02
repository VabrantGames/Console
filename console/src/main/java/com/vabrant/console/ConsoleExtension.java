
package com.vabrant.console;

import com.vabrant.console.events.Event;
import com.vabrant.console.events.EventListener;
import com.vabrant.console.events.EventManager;
import com.vabrant.console.log.Log;
import com.vabrant.console.log.LogLevel;
import com.vabrant.console.log.LogManager;

public abstract class ConsoleExtension implements Executable<Object, Boolean> {

	protected Console console;
	protected EventManager eventManager;
	protected LogManager logManager;

	protected ConsoleExtension () {
		eventManager = new EventManager();
	}

	protected final void setConsole (Console console) {
		this.console = console;
	}

	public <T extends Event> void subscribeToEvent (Class<T> event, EventListener<T> listener) {
		eventManager.subscribe(event, listener);
	}

	public <T extends Event> void unsubscribeFromEvent (Class<T> event, EventListener<T> listener) {
		eventManager.subscribe(event, listener);
	}

	public <T extends Event> void fireEvent (Class<T> type, T event) {
		eventManager.fire(type, event);
	}

	public <T extends Event> void postFireEvent (Class<T> type, T event) {
		eventManager.postFire(type, event);
	}

	public String getName() {
		return null;
	}

	public EventManager getEventManager() {
		return eventManager;
	}

	protected void addedToConsole (Console console) {

	}

	protected void removedFromConsole (Console console) {

	}

	public Log log (String message, LogLevel level) {
		return log(null, message, level);
	}

	public Log log (String tag, String message, LogLevel level) {
		return log(tag, message, level, false);
	}

	public Log log (String tag, String message, LogLevel level, boolean indent) {
		Log log = null;

		if (logManager != null) {
			log = logManager.create(tag, message, level);
			log.indent(indent);
			logManager.add(log);
		}

		return log;
	}

//	@Override
//	public Boolean execute (Object o) throws Exception {
//		return null;
//	}

//	@Override
//	public abstract Boolean execute (Object o) throws Exception;
}
