
package com.vabrant.console.gui.events;

import com.badlogic.gdx.utils.Array;
import com.vabrant.console.events.Event;
import com.vabrant.console.events.EventListener;
import com.vabrant.console.gui.FocusObject;

public class GUIConsoleFocusEvent implements Event {

	private FocusObject focusObject;

	public void setFocusObject (FocusObject focusObject) {
		this.focusObject = focusObject;
	}

	public FocusObject getFocusObject () {
		return focusObject;
	}

	@Override
	public <T extends Event> void handle (Array<EventListener<T>> eventListeners) {
		for (EventListener l : eventListeners) {
			l.handleEvent(this);
		}
	}
}
