
package com.vabrant.console.events;

import com.badlogic.gdx.utils.Array;

public abstract class DefaultEvent implements Event {
	@Override
	public <T extends Event> void handle (Array<EventListener<T>> eventListeners) {
		for (EventListener l : eventListeners) {
			l.handleEvent(this);
		}
	}
}
