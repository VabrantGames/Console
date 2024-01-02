package com.vabrant.console.gui.events;

import com.badlogic.gdx.utils.Array;
import com.vabrant.console.events.Event;
import com.vabrant.console.events.EventListener;

public class GUIConsoleUnfocusEvent implements Event {
	@Override
	public <T extends Event> void handle (Array<EventListener<T>> eventListeners) {
		for (EventListener l : eventListeners) {
			l.handleEvent(this);
		}
	}
}
