package com.vabrant.console.events;

import com.badlogic.gdx.utils.Array;

public class DefaultTargetEvent implements Event{
	@Override
	public <T extends Event> void handle (Array<EventListener<T>> eventListeners) {
		for (EventListener l : eventListeners) {
				if (l.getTarget() == null || !this.equals(l.getTarget())) continue;
				l.handleEvent(this);
			}
	}
}
