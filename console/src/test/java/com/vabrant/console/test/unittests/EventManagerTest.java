
package com.vabrant.console.test.unittests;

import com.vabrant.console.events.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class EventManagerTest {

	private static EventManager manager;

	@BeforeAll
	public static void init () {
		manager = new EventManager();

		// The events the manager can handle
		manager.registerEvents(TestEvent.class, TestTargetEvent.class);
	}

	@Test
	void BasicTest () {
		TestEvent event = new TestEvent();

		manager.subscribe(TestEvent.class, (e) -> System.out.println("Hello"));
		manager.subscribe(TestEvent.class, (e) -> System.out.println("Goodbye"));

		manager.fire(TestEvent.class, event);
	}

	@Test
	void TargetTest () {
		TestTargetEvent event1 = new TestTargetEvent();
		TestTargetEvent event2 = new TestTargetEvent();

		TargetEventListener<TestTargetEvent> listener = new TargetEventListener<TestTargetEvent>(event1) {

			@Override
			public void handleEvent (TestTargetEvent targetEvent) {
				System.out.println("Hello: " + targetEvent.getClass().getSimpleName());
			}
		};

		TargetEventListener<TestTargetEvent> shouldNotBeCalledListener = new TargetEventListener<TestTargetEvent>(event2) {

			@Override
			public void handleEvent (TestTargetEvent targetEvent) {
				throw new RuntimeException("Should not be called");
			}
		};

		manager.subscribe(TestTargetEvent.class, listener);
		manager.subscribe(TestTargetEvent.class, shouldNotBeCalledListener);

		manager.fire(TestTargetEvent.class, event1);
	}

	private static class TestEvent extends DefaultEvent {
	}

	private static class TestTargetEvent extends DefaultTargetEvent {

		TestTargetEvent () {
		}
	}
}
