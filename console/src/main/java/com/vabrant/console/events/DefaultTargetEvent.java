
package com.vabrant.console.events;

import com.badlogic.gdx.utils.Array;

public abstract class DefaultTargetEvent implements Event {

	private Object source;

	protected DefaultTargetEvent () {
		source = this;
	}

	protected DefaultTargetEvent (Object source) {
		if (source == null) throw new IllegalArgumentException("Source object can't be null");
		this.source = source;
	}

	@Override
	public <T extends Event> void handle (Array<EventListener<T>> eventListeners) {
		for (EventListener l : eventListeners) {
			if (l.getTarget() == null || !source.equals(l.getTarget())) continue;
			l.handleEvent(this);
		}
	}
}
