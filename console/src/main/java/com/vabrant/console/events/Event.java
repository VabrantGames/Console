package com.vabrant.console.events;

import com.badlogic.gdx.utils.Array;

public interface Event {

	<T extends Event> void handle (Array<EventListener<T>> listeners);
}
