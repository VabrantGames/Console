
package com.vabrant.console.events;

public interface EventListener<T> {
	void handleEvent (T t);

	default Object getTarget () {
		return null;
	}
}
