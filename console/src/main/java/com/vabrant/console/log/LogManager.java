
package com.vabrant.console.log;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pools;
import com.vabrant.console.EventListener;
import com.vabrant.console.EventManager;

public class LogManager {

	public static final String ADD_LOG_EVENT = "add_log_event";

	private final int maxEntries;
	private final Array<Log> entries;
	private final EventManager eventManager;

	public LogManager () {
		this(100);
	}

	public LogManager (int maxEntries) {
		this.maxEntries = maxEntries;
		entries = new Array<>(maxEntries);
		eventManager = new EventManager(ADD_LOG_EVENT);
	}

	public Log getNewLog () {
		Log log = Pools.obtain(Log.class);
		log.stampTime();
		return log;
	}

	public void subscribeToEvent (String event, EventListener<LogManager> listener) {
		eventManager.subscribe(event, listener);
	}

	public Log create (String tag, String message, LogLevel level) {
		return getNewLog().setTag(tag).setMessage(message).setLogLevel(level);
	}

	public void add (String tag, String message, LogLevel level) {
		add(create(tag, message, level));
	}

	public void add (Log log) {
		if ((entries.size + 1) > maxEntries) {
			Pools.free(entries.removeIndex(0));
		}

		entries.add(log);
		eventManager.fire(ADD_LOG_EVENT, this);
	}

	public Array<Log> getEntries () {
		return entries;
	}

	public interface LogManagerEventListener extends EventListener<LogManager> {

	}
}
