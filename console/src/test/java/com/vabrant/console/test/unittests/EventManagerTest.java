package com.vabrant.console.test.unittests;

import com.badlogic.gdx.utils.Array;
import com.vabrant.console.events.Event;
import com.vabrant.console.events.EventListener;
import com.vabrant.console.events.EventManager;
import com.vabrant.console.events.TargetEventListener;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class EventManagerTest {

	private static EventManager manager;

	@BeforeAll
	public static void init() {
		manager = new EventManager();

		// The events the manager can handle
		manager.addEvents(DefaultEvent.class, TargetEvent.class);
	}

	@Test
	void BasicTest() {
		DefaultEvent event = new DefaultEvent();

		manager.subscribe(DefaultEvent.class, (e) -> System.out.println("Hello"));
		manager.subscribe(DefaultEvent.class, (e) -> System.out.println("Goodbye"));

		manager.fire(DefaultEvent.class, event);
	}

	@Test
	void TargetTest() {
		TargetEvent event1 = new TargetEvent();
		TargetEvent event2 = new TargetEvent();

		TargetEventListener<TargetEvent> listener = new TargetEventListener<TargetEvent>(event1) {

			@Override
			public void handleEvent (TargetEvent targetEvent) {
				System.out.println("Hello: " + targetEvent.getClass().getSimpleName());
			}
		};

		TargetEventListener<TargetEvent> shouldNotBeCalledListener = new TargetEventListener<TargetEvent>(event2) {

			@Override
			public void handleEvent (TargetEvent targetEvent) {
				throw new RuntimeException("Should not be called");
			}
		};

		manager.subscribe(TargetEvent.class, listener);
		manager.subscribe(TargetEvent.class, shouldNotBeCalledListener);

		manager.fire(TargetEvent.class, event1);
	}

	private static class DefaultEvent implements Event {
		@Override
		public <T extends Event> void handle (Array<EventListener<T>> eventListeners) {
			for (EventListener l : eventListeners) {
				l.handleEvent(this);
			}
		}
	}

	private static class TargetEvent implements Event {
		@Override
		public <T extends Event> void handle (Array<EventListener<T>> eventListeners) {
			for (EventListener l : eventListeners) {
				if (l.getTarget() == null || !this.equals(l.getTarget())) continue;

				l.handleEvent(this);
			}
		}
	}
}
