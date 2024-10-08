
package com.vabrant.console;

import com.vabrant.console.commandexecutor.CommandCache;
import com.vabrant.console.commandexecutor.DefaultCommandCache;
import com.vabrant.console.events.Event;
import com.vabrant.console.events.EventListener;
import com.vabrant.console.events.EventManager;
import com.vabrant.console.log.Log;
import com.vabrant.console.log.LogLevel;
import com.vabrant.console.log.LogManager;

public abstract class ConsoleExtension {

	protected final String name;
	protected Console console;
	protected EventManager eventManager;
	protected CommandCache commandCache;
	protected LogManager logManager;

	protected ConsoleExtension (String name) {
		this(name, null);
	}

	protected ConsoleExtension (String name, CommandCache commandCache) {
		this.name = name;
		eventManager = new EventManager();
		logManager = new LogManager(100, eventManager);
		this.commandCache = commandCache != null ? commandCache : new DefaultCommandCache();
	}

	public final void setConsole (Console console) {
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

	public String getName () {
		return name;
	}

	public EventManager getEventManager () {
		return eventManager;
	}

	protected void addedToConsole (Console console) {

	}

	protected void removedFromConsole (Console console) {

	}

	public boolean execute (Object o) throws Exception {
		return console.getCommandExecutor().execute(commandCache, o).getExecutionStatus();
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

}
