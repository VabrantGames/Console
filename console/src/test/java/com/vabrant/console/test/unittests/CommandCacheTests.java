
package com.vabrant.console.test.unittests;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.graphics.Color;
import com.vabrant.console.CommandEngine.*;
import com.vabrant.console.ConsoleRuntimeException;
import com.vabrant.console.DebugLogger;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CommandCacheTests {

	private static TestClass testClass;
	private static Application application;

	@BeforeAll
	public static void init () {
		application = new HeadlessApplication(new ApplicationAdapter() {});
		testClass = new TestClass();
		Gdx.app.setLogLevel(Application.LOG_DEBUG);
	}

	public static void printTestHeader (String name) {
		System.out.println();
		System.out.println("//----------//" + ' ' + name + ' ' + "//----------//");
	}

	@Test
	void AddInstanceReferenceTest () {
		printTestHeader("Add Instance Reference Test");

		final String name = "Bob";
		DefaultCommandCache cache = new DefaultCommandCache();
		cache.getLogger().setLevel(DebugLogger.DEBUG);

		// Add a new Object
		cache.addReference(name, testClass);

		assertTrue(cache.hasReference(name));

		// Add the same object but with a different name.
		assertThrows(ConsoleRuntimeException.class, () -> cache.addReference("Green", testClass));

		// Add a different object but with the same name.
		assertThrows(ConsoleRuntimeException.class, () -> cache.addReference(name, new TestClass()));
	}

	@Test
	void AddStaticReferenceTest () {
		printTestHeader("Add Static Reference Test");

		final String name = "Utils";
		DefaultCommandCache cache = new DefaultCommandCache();
		cache.getLogger().setLevel(DebugLogger.DEBUG);
		cache.addReference(name, TestClass.class);

		assertTrue(cache.hasReference(name));
		assertTrue(cache.getReference(name) instanceof StaticReference);

		// Add the same class but different name
		assertThrows(RuntimeException.class, () -> cache.addReference("bob", TestClass.class));

		// Add a different class but same name
		assertThrows(RuntimeException.class, () -> cache.addReference(name, String.class));
	}

	@Test
	void AddInstanceMethodTest () {
		printTestHeader("Add Instance Method Test");

		DefaultCommandCache cache = new DefaultCommandCache();
		cache.getLogger().setLevel(DebugLogger.DEBUG);

		ClassReference<?> ref = cache.addReference("test", testClass);
		cache.addCommand(ref, "print");
		cache.addCommand(ref, "print", String.class);

		assertTrue(cache.hasCommand((ClassReference<?>)null, "print", String.class));
		assertTrue(cache.hasCommand((ClassReference<?>)null, "print"));
		assertTrue(cache.hasCommand(ref, "print", String.class));
		assertTrue(cache.hasCommand(ref, "print", null));
	}

	@Test
	void AddStaticMethodTest () {
		printTestHeader("Add Static Method Test");

		DefaultCommandCache cache = new DefaultCommandCache();
		cache.getLogger().setLevel(DebugLogger.DEBUG);

		ClassReference<?> ref = cache.addReference("TestClass", TestClass.class);

		cache.addCommand(ref, "global");

		assertTrue(cache.hasReference("TestClass"));
		assertTrue(cache.hasCommand(ref, "global", null));

		// Try to add a non static method
		assertThrows(ConsoleRuntimeException.class, () -> cache.addCommand(ref, "print"));
	}

	@Test
	void AddTestAnnotations () {
		printTestHeader("Add Test Annotations");

		DefaultCommandCache cache = new DefaultCommandCache();
		cache.getLogger().setLevel(DebugLogger.DEBUG);
		ClassReference<?> ref = cache.addReference("test", testClass);
		cache.addAll(ref);

		assertTrue(cache.hasReference("test"));
		assertTrue(cache.hasReference("name"));
		assertTrue(cache.hasReference("red"));
	}

	@ConsoleReference("tc")
	public static class TestClass {
		public void print () {
		}

		public void print (String m) {
		}

		public static void global () {
		};

		@ConsoleCommand
		public void multi (String s, int i, double d) {
		}

		@ConsoleReference("name") public final String name = "console";

		@ConsoleReference("red") public final Color color = new Color(1, 0, 0, 1);

		@ConsoleCommand
		public void hello (String name) {
		}

		@ConsoleCommand
		public void changeColor (Color color) {
		}

	}

}
