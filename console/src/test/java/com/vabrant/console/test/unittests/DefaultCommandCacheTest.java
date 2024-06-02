
package com.vabrant.console.test.unittests;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.vabrant.console.CommandEngine.ClassReference;
import com.vabrant.console.CommandEngine.Command;
import com.vabrant.console.CommandEngine.DefaultCommandCache;
import com.vabrant.console.ConsoleRuntimeException;
import com.vabrant.console.DebugLogger;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DefaultCommandCacheTest {

	private static Application application;

	@Test
	void ReferenceTest () {
		application = new HeadlessApplication(new ApplicationAdapter() {});
		Gdx.app.setLogLevel(Application.LOG_DEBUG);

		DefaultCommandCache cache = new DefaultCommandCache();
		cache.getLogger().setLevel(DebugLogger.DEBUG);

		final String ID = "test";
		cache.addReference(ID, new TestObject());

		assertNotNull(cache.getReference(ID));
		assertThrows(Exception.class, () -> {
			cache.addReference(ID, new TestObject());
		});
	}

	@Test
	void CommandTest () {
		DefaultCommandCache cache = new DefaultCommandCache();
		cache.getLogger().setLevel(DebugLogger.DEBUG);

		final String ID = "test";
		cache.addCommand(ID, new TestCommand());

		assertNotNull(cache.getCommand(ID));
	}

	@Test
	void MethodCommandTest () {
		DefaultCommandCache cache = new DefaultCommandCache();
		cache.getLogger().setLevel(DebugLogger.DEBUG);

		final String ID = "test";
		ClassReference ref = cache.addReference(ID, new TestObject());
		cache.addCommand(ref, "hello", int.class);
		cache.addCommand(ref, "hello", String.class);

		assertNotNull(cache.getCommand(ref, "hello", int.class));
		assertNotNull(cache.getCommand(ref, "hello", String.class));
	}

	/*
	 * Only one non method command is allowed per namew
	 */
	@Test
	void TwoNonMethodCommandsTest () {
		DefaultCommandCache cache = new DefaultCommandCache();
		cache.getLogger().setLevel(DebugLogger.DEBUG);

		final String ID = "test";

		cache.addCommand("hello", new TestCommand());

		assertThrows(ConsoleRuntimeException.class, () -> cache.addCommand("hello", new TestCommand()));
	}

	class TestObject {

		public void hello (int arg) {
			System.out.println("Hello: " + arg);
		}

		public void hello (String s) {
			System.out.println("String: " + s);
		}
	}

	class TestCommand implements Command {

		@Override
		public void setSuccessMessage (String message) {

		}

		@Override
		public String getSuccessMessage () {
			return null;
		}

		@Override
		public Class getReturnType () {
			return null;
		}

		@Override
		public Object execute (Object[] args) throws Exception {
			return null;
		}
	}
}
