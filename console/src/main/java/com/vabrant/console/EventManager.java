
package com.vabrant.console;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

public class EventManager {

	private ObjectMap<String, Array<EventListener>> events;

	public EventManager (String... eventTypes) {
		events = new ObjectMap<>();

		if (eventTypes != null) {
			for (String s : eventTypes) {
				addEvent(s);
			}
		}
	}

	public void addEvent (String event) {
		if (events.containsKey(event)) return;
		events.put(event, new Array<>());
	}

	public void removeAllListeners(String event) {
		if (events.containsKey(event)) {
			events.remove(event);
		}
	}

	public void subscribe (String event, EventListener listener) {
		if (!events.containsKey(event)) return;
		Array<EventListener> listeners = events.get(event);
		if (listeners.contains(listener, false)) return;
		listeners.add(listener);
	}

	public void unsubscribe (String event, EventListener listener) {
		if (!events.containsKey(event)) return;
		events.get(event).removeValue(listener, false);
	}

	public <T> void fire (String event, T data) {
		Array<EventListener> listeners = events.get(event);
		if (listeners == null) return;
		for (EventListener e : listeners) {
			e.handleEvent(data);
		}
	}
}
