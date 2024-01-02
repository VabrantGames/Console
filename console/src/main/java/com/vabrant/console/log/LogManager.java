
package com.vabrant.console.log;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pools;
import com.vabrant.console.events.Event;
import com.vabrant.console.events.EventListener;
import com.vabrant.console.events.EventManager;

public class LogManager {

	private final int maxEntries;
	private final Array<Log> allEntries;
	private final Array<Log> filteredEntries;
	private LogManagerEvent event;
	private EventManager eventManager;
	private Array<LogManagerChangeListener> changeListeners;

	public LogManager () {
		this(100);
	}

	public LogManager (int maxEntries) {
		this(100, null);
	}

	public LogManager (int maxEntries, EventManager eventManager) {
		this.maxEntries = maxEntries;
		this.eventManager = eventManager;
		changeListeners = new Array<>();
		allEntries = new Array<>(maxEntries);
		filteredEntries = new Array<>();
		event = new LogManagerEvent();

		if (eventManager != null) {
			eventManager.addEvent(LogManagerEvent.class);
		}
	}

	public void addChangeListener (LogManagerChangeListener listener) {
		changeListeners.add(listener);
//		this.listener = listener;
	}

	public Log getNewLog () {
		Log log = Pools.obtain(Log.class);
		log.stampTime();
		return log;
	}

	public Log create (String tag, String message, LogLevel level) {
		return getNewLog().setTag(tag).setMessage(message).setLogLevel(level);
	}

	public void add (String tag, String message, LogLevel level) {
		add(create(tag, message, level));
	}

	public void add (Log log) {
		if ((allEntries.size + 1) > maxEntries) {
			Pools.free(allEntries.removeIndex(0));
		}

		allEntries.add(log);

		for (LogManagerChangeListener l : changeListeners) {
			l.onChange();
		}
//		if (listener != null) listener.onChange();

		if (eventManager != null) {
			eventManager.postFire(LogManagerEvent.class, event);
		}
	}

	public Array<Log> getAllEntries () {
		return allEntries;
	}

	public interface LogManagerChangeListener {
		void onChange();
	}

	public static class LogManagerEvent implements Event {

		private LogManager source;
		private EventManager eventManager;


		@Override
		public <T extends Event> void handle (Array<EventListener<T>> eventListeners) {

			for (EventListener<T> l : eventListeners) {

			}
		}
	}
}
