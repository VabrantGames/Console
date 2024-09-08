
package com.vabrant.console.test.unittests;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.vabrant.console.commandexecutor.*;
import com.vabrant.console.ConsoleRuntimeException;
import com.vabrant.console.DebugLogger;
import com.vabrant.console.test.ConsoleTestsUtils.CustomCommand;
import com.vabrant.console.test.ConsoleTestsUtils.TestClass;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DefaultCommandCacheTest {

	private static Application application;

	@BeforeAll
	public static void init () {
		application = new HeadlessApplication(new ApplicationAdapter() {});
		Gdx.app.setLogLevel(Application.LOG_DEBUG);
	}

	@Test
	void ReferenceTest () {
		DefaultCommandCache cache = new DefaultCommandCache();
		cache.setLogLevel(DebugLogger.DEBUG);

		ClassReference ref = cache.addReference("ref", new TestClass());

		assertTrue(cache.hasReference("ref"));

		// Attempt to create a reference with the same ID
		assertThrows(Exception.class, () -> {
			cache.addReference("ref", new TestClass());
		});
	}

	@Test
	void CommandTest () {
		DefaultCommandCache cache = new DefaultCommandCache();
		cache.setLogLevel(DebugLogger.DEBUG);

		CustomCommand customCommand = new CustomCommand();
		cache.addCommand("hello", customCommand);
		assertEquals(customCommand, cache.getCommand("hello"));

		// Try to create another hello command
		assertThrows(ConsoleRuntimeException.class, () -> cache.addCommand("hello", new CustomCommand()));

		cache.addMethodCommand(cache.addReference("ref", new TestClass()), "hello", String.class);
		assertNotNull(cache.getMethodCommand("ref", "hello", String.class));

		// Try to create MethodCommand with the same reference
		assertThrows(ConsoleRuntimeException.class, () -> cache.addMethodCommand(cache.getReference("ref"), "hello", String.class));

		// Create another 0 argument MethodCommand but with a different reference
		assertDoesNotThrow( () -> cache.addMethodCommand(cache.addReference("ref2", new TestClass()), "hello"));
	}

	@Test
	void GetCommandPriorityTest () {
		DefaultCommandCache cache = new DefaultCommandCache();
		cache.setLogLevel(DebugLogger.DEBUG);

		Command customCommand = new CustomCommand();
		cache.addCommand("hello", customCommand);
		ClassReference ref = cache.addReference("ref", new TestClass());
		cache.addMethodCommand(ref, "hello", String.class);
		cache.addMethodCommand(ref, "hello", int.class);

		assertEquals(customCommand, cache.getCommand("hello"));
	}

	@Test
	void AddAllTest () {
		DefaultCommandCache cache = new DefaultCommandCache();
		cache.setLogLevel(DebugLogger.DEBUG);

		class ClassWithNoAnnotation {
			@ConsoleReference("hello") public String hello = "Hello";
		}

		cache.addAll(new ClassWithNoAnnotation());

		assertNotNull(cache.getReference("hello"));

		cache.addAll(new TestClass());

		assertNotNull(cache.getReference("helloClass"));
		assertNotNull(cache.getMethodCommand("helloClass", "hello", String.class));

		cache.addAll(cache.addReference("static", TestClass.class));
		assertNotNull(cache.getMethodCommand("static", "helloAll"));
	}

	@Test
	void AddAllIncludeStaticCommandsTest () {
		DefaultCommandCache cache = new DefaultCommandCache();
		cache.setIncludeStaticCommandsForInstanceReferences();

		cache.addAll(new TestClass());

		assertNotNull(cache.getMethodCommand("helloClass", "helloAll"));
	}

}
