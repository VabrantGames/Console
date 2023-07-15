
package com.vabrant.console.commandextension;

import com.vabrant.console.EventManager;
import com.vabrant.console.ExecutionData;
import com.vabrant.console.log.Log;
import com.vabrant.console.log.LogLevel;
import com.vabrant.console.log.LogManager;

public class CommandData extends ExecutionData<CommandStrategy> {

	public static final String FAIL_EVENT = "fail";
	public static final String SUCCESS_EVENT = "success";

	private CommandCache cache;
	private CommandEvent event;
	private EventManager eventManager;
	private CommandSettings settings;
	private LogManager logManager;

	public CommandData () {
		this(null, null);
	}

	public CommandData (LogManager logManager, CommandSettings settings) {
		this.logManager = logManager;
		this.settings = settings;
		eventManager = new EventManager(FAIL_EVENT, SUCCESS_EVENT);
		event = new CommandEvent(this);
	}

// public void setSettings (GUICommandConsoleConfiguration settings) {
// this.settings = settings;
// }

	public CommandSettings getSettings () {
		if (settings == null) {
			settings = new CommandSettings();
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
		log(null, message, level, false);
	}

	public void log (String tag, String message, LogLevel level) {
		log(tag, message, level, false);
	}

	public void log (String tag, String message, LogLevel level, boolean indent) {
		Log log = null;
		if (logManager != null) {
			log = logManager.create(tag, message, level);
			log.indent(indent);
			logManager.add(log);
		}
	}

	public CommandEvent getEvent () {
		return event;
	}

	public EventManager getEventManager () {
		return eventManager;
	}

	public void setConsoleCache (CommandCache cache) {
		this.cache = cache;
		event.clear();
	}

	public CommandCache getConsoleCache () {
		return cache;
	}
}
