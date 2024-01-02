package com.vabrant.console.events;

import com.badlogic.gdx.utils.Array;
import com.vabrant.console.events.Event;
import com.vabrant.console.events.EventListener;

public class DefaultEvent implements Event {
	@Override
	public <T extends Event> void handle (Array<EventListener<T>> eventListeners) {
		for (EventListener l : eventListeners) {
			l.handleEvent(this);
		}
	}
}
