
package com.vabrant.console.log;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pools;
import com.vabrant.console.ConsoleRuntimeException;
import com.vabrant.console.events.*;

public class LogManager {

	private final int maxEntries;
	private final Array<Log> allEntries;
	private final Array<Log> filteredEntries;
	private LogManagerAddEvent addEvent;
	private LogManagerRemoveEvent removeEvent;
	private EventManager eventManager;
// private Array<LogManagerChangeListener> changeListeners;

	public LogManager () {
		this(100);
	}

	public LogManager (int maxEntries) {
		this(100, null);
	}

	public LogManager (int maxEntries, EventManager eventManager) {
		this.maxEntries = maxEntries;
		this.eventManager = eventManager;
// changeListeners = new Array<>();
		allEntries = new Array<>(maxEntries);
		filteredEntries = new Array<>();
		addEvent = new LogManagerAddEvent(this);
		removeEvent = new LogManagerRemoveEvent(this);

		if (eventManager != null) {
			eventManager.registerEvent(LogManagerAddEvent.class);
			eventManager.registerEvent(LogManagerRemoveEvent.class);
		}
	}

	public <T extends LogManagerEvent, U extends LogManagerEventListener<T>> void subscribeToEvent (Class<T> klass, U listener) {
		if (eventManager == null) throw new ConsoleRuntimeException("No event manager set");
		listener.setTarget(this);
		eventManager.subscribe(klass, listener);
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
			Log logToRemove = allEntries.removeIndex(0);

			if (eventManager != null) {
				removeEvent.log = logToRemove;
				eventManager.postFire(LogManagerRemoveEvent.class, removeEvent);
			}

			Pools.free(logToRemove);
// Pools.free(allEntries.removeIndex(0));
		}

		allEntries.add(log);

// for (LogManagerChangeListener l : changeListeners) {
// l.onChange();
// }

		if (eventManager != null) {
			addEvent.log = log;
			eventManager.postFire(LogManagerAddEvent.class, addEvent);
		}
	}

	public Array<Log> getAllEntries () {
		return allEntries;
	}

	public abstract static class LogManagerEventListener<T> extends TargetEventListener<T> {

		protected LogManagerEventListener () {
			super(null);
		}
	}

	public abstract static class LogManagerEvent extends DefaultTargetEvent {

		protected Log log;

		protected LogManagerEvent (LogManager source) {
			super(source);
		}
	}

	public static class LogManagerAddEvent extends LogManagerEvent {

		public LogManagerAddEvent (LogManager source) {
			super(source);
		}
	}

	public static class LogManagerRemoveEvent extends LogManagerEvent {

		public LogManagerRemoveEvent (LogManager source) {
			super(source);
		}
	}

}
