
package com.vabrant.console;

import com.vabrant.console.events.Event;
import com.vabrant.console.events.EventListener;
import com.vabrant.console.events.EventManager;
import com.vabrant.console.log.LogManager;

public interface Console {

	void addExtension (ConsoleExtension extension);

	void setActiveExtension (ConsoleExtension extension);

	ConsoleExtension getActiveExtension ();

	ConsoleExtension getExtension (String name);

	EventManager getEventManager ();

	DebugLogger getLogger ();

	LogManager getLogManager ();

	boolean execute (Object o);

	boolean execute (ConsoleExtension extension, Object input);

	<T extends Event> void subscribeToEvent (Class<T> event, EventListener<T> listener);

	<T extends Event> boolean unsubscribeFromEvent (Class<T> event, EventListener<T> listener);

	<T extends Event> void fireEvent (Class<T> type, T event);

	<T extends Event> void postFireEvent (Class<T> type, T event);
}
