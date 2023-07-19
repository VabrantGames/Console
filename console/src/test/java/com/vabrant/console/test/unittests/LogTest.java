
package com.vabrant.console.test.unittests;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.vabrant.console.log.LogLevel;
import com.vabrant.console.log.LogManager;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LogTest {

	private static Application application;

	@BeforeAll
	public static void init () {
		application = new HeadlessApplication(new ApplicationAdapter() {});
	}

	@Test
	void basicTet () {
		LogManager manager = new LogManager();
		manager.add("Hello", "Info", LogLevel.INFO);
		manager.add("Hello", "Error", LogLevel.ERROR);
		manager.add("Hello", "Debug", LogLevel.DEBUG);

		assertEquals(3, manager.getEntries().size);
	}

}
