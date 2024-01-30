
package com.vabrant.console.events;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.vabrant.console.ConsoleRuntimeException;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class EventManager {

	private Map<Class<? extends Event>, Array> events;

	public EventManager () {
		this(null);
	}

	public EventManager (Class<? extends Event>... eventTypes) {
		events = new ConcurrentHashMap<>();

		registerEvents(eventTypes);
	}

	public boolean contains (Class<? extends Event> event) {
		return events.containsKey(event);
	}

	public void registerEvent (Class<? extends Event> event) {
		if (contains(event)) return;
		events.put(event, new Array<>());
	}

	public void registerEvents (Class<? extends Event>... events) {
		if (events == null) return;

		for (Class c : events) {
			registerEvent(c);
		}
	}

	public void unregisterEvent (Class<? extends Event> event) {
		events.remove(event);
	}

	public <T extends Event> void subscribe (Class<T> type, EventListener<T> listener) {
		if (!contains(type)) throw new ConsoleRuntimeException("No event registered for " + type.getCanonicalName());
		Array<EventListener<?>> listeners = events.get(type);
		if (listeners.contains(listener, false)) return;
		listeners.add(listener);
	}

	public <T extends Event> boolean unsubscribe (Class<T> event, EventListener<T> listener) {
		if (!contains(event)) return false;
		return events.get(event).removeValue(listener, false);
	}

	public <T extends Event> void fire (Class<T> type, T event) {
		Array<EventListener<T>> listeners = events.get(type);
		if (listeners == null) return;
		event.handle(listeners);
	}

	public <T extends Event> void postFire (Class<T> event, T data) {
		Gdx.app.postRunnable( () -> fire(event, data));
	}

	public void removeListeners (Class<? extends Event> event) {
		events.remove(event);
	}

	public void clear () {
		events.clear();
	}
}
