
package com.vabrant.console;

import com.vabrant.console.commandextension.CommandExecutionEvent;
import com.vabrant.console.commandextension.CommandExtensionSettings;
import com.vabrant.console.log.LogLevel;
import com.vabrant.console.log.LogManager;

public class CommandExecutionData extends ExecutionData<CommandExecutionStrategy> {

	public static final String FAIL_EVENT = "fail";
	public static final String SUCCESS_EVENT = "success";

	private ConsoleCache cache;
	private CommandExecutionEvent event;
	private EventManager eventManager;
	private CommandExtensionSettings settings;
	private LogManager logManager;

	public CommandExecutionData () {
		this(null, null);
	}

	public CommandExecutionData (LogManager logManager, CommandExtensionSettings settings) {
		this.logManager = logManager;
		this.settings = settings;
		eventManager = new EventManager(FAIL_EVENT, SUCCESS_EVENT);
		event = new CommandExecutionEvent(this);
	}

// public void setSettings (GUICommandConsoleConfiguration settings) {
// this.settings = settings;
// }

	public CommandExtensionSettings getSettings () {
		if (settings == null) {
			settings = new CommandExtensionSettings();
		}
		return settings;
	}

// public void subscribeToEvent (String event, EventListener<CommandExecutionEvent> listener) {
// eventManager.subscribe(event, listener);
// }
//
// public void unsubscribeFromEvent (String event, EventListener<CommandExecutionEvent> listener) {
// eventManager.unsubscribe(event, listener);
// }
//
// public void fireEvent (String type, CommandExecutionEvent event) {
// eventManager.fire(type, event);
// }

	public void log (String message, LogLevel level) {
		log(null, message, level);
	}

	public void log (String tag, String message, LogLevel level) {
		if (logManager != null) logManager.add(tag, message, level);
	}

	public CommandExecutionEvent getEvent () {
		return event;
	}

	public EventManager getEventManager () {
		return eventManager;
	}

	public void setConsoleCache (ConsoleCache cache) {
		this.cache = cache;
		event.clear();
	}

	public ConsoleCache getConsoleCache () {
		return cache;
	}
}
