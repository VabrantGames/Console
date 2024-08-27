
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
	private LogManagerRefreshEvent refreshEvent;
	private EventManager eventManager;
	private LogManagerFilter filter;
	private NoFilterFilter noFilterFilter;
	private LogLevelFilter logLevelFilter;
	private LogTagFilter logTagFilter;

	public LogManager () {
		this(100);
	}

	public LogManager (int maxEntries) {
		this(100, null);
	}

	public LogManager (int maxEntries, EventManager eventManager) {
		this.maxEntries = maxEntries;
		this.eventManager = eventManager;
		allEntries = new Array<>(maxEntries);
		filteredEntries = new Array<>();
		addEvent = new LogManagerAddEvent(this);
		removeEvent = new LogManagerRemoveEvent(this);
		refreshEvent = new LogManagerRefreshEvent(this);
		noFilterFilter = new NoFilterFilter();
		logLevelFilter = new LogLevelFilter(this);
		logTagFilter = new LogTagFilter(this);

		filter = noFilterFilter;

		if (eventManager != null) {
			eventManager.registerEvent(LogManagerAddEvent.class);
			eventManager.registerEvent(LogManagerRemoveEvent.class);
			eventManager.registerEvent(LogManagerRefreshEvent.class);
		}
	}

	public <T extends LogManagerEvent, U extends LogManagerEventListener<T>> void subscribeToEvent (Class<T> klass, U listener) {
		if (eventManager == null) throw new ConsoleRuntimeException("No event manager set");
		listener.setTarget(this);
		eventManager.subscribe(klass, listener);
	}

	public void setFilter (LogManagerFilter filter) {
		if (filter == null) {
			filter = noFilterFilter;
		} else if (this.filter.equals(filter)) {
			return;
		}

		this.filter = filter;
		refresh();
	}

	public void refresh (LogManagerFilter f) {
		if (filter.equals(f)) {
			refresh();
		}
	}

	private void refresh () {
		filteredEntries.clear();

		for (Log l : allEntries) {
			if (filter.accept(l)) {
				filteredEntries.add(l);
			}
		}

		if (eventManager != null) {
			eventManager.postFire(LogManagerRefreshEvent.class, refreshEvent);
		}
	}

	public void filterByTag (String tag) {
		logTagFilter.setTag(tag);
		setFilter(logTagFilter);
	}

	public void filterByLevel (LogLevel level) {
		logLevelFilter.setLevel(level);
		setFilter(logLevelFilter);
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

			filteredEntries.removeValue(logToRemove, false);

			if (eventManager != null) {
				removeEvent.log = logToRemove;
				eventManager.postFire(LogManagerRemoveEvent.class, removeEvent);
			}

			Pools.free(logToRemove);
		}

		allEntries.add(log);

		if (filter.accept(log)) {
			filteredEntries.add(log);
			addEvent.filtered = true;
		} else {
			addEvent.filtered = false;
		}

		if (eventManager != null) {
			addEvent.log = log;
			eventManager.postFire(LogManagerAddEvent.class, addEvent);
		}
	}

	public Array<Log> getAllEntries () {
		return allEntries;
	}

	public Array<Log> getFilteredEntries () {
		return filteredEntries;
	}

	public interface LogManagerFilter {
		boolean accept (Log log);
	}

	private class NoFilterFilter implements LogManagerFilter {

		@Override
		public boolean accept (Log log) {
			return true;
		}
	}

	private class LogLevelFilter implements LogManagerFilter {

		private LogLevel level;
		private LogManager manager;

		public LogLevelFilter (LogManager manager) {
			this.manager = manager;
		}

		public void setLevel (LogLevel level) {
			this.level = level;
			manager.refresh(this);
		}

		@Override
		public boolean accept (Log log) {
			return false;
		}
	}

	private class LogTagFilter implements LogManagerFilter {

		private String tag;
		private LogManager manager;

		public LogTagFilter (LogManager manager) {
			this.manager = manager;
		}

		public void setTag (String tag) {
			this.tag = tag;
			manager.refresh(this);
		}

		@Override
		public boolean accept (Log log) {
			if (log.getTag() == null) return false;
			return log.getTag().equals(tag);
		}
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

	public static class LogManagerRefreshEvent extends LogManagerEvent {

		protected LogManagerRefreshEvent (LogManager source) {
			super(source);
		}
	}

	public static class LogManagerAddEvent extends LogManagerEvent {

		private boolean filtered;

		public LogManagerAddEvent (LogManager source) {
			super(source);
		}

		public boolean filtered () {
			return filtered;
		}
	}

	public static class LogManagerRemoveEvent extends LogManagerEvent {

		public LogManagerRemoveEvent (LogManager source) {
			super(source);
		}
	}

}
