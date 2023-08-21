
package com.vabrant.console.test.unittests;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.graphics.Color;
import com.vabrant.console.ConsoleRuntimeException;
import com.vabrant.console.commandextension.ClassReference;
import com.vabrant.console.commandextension.CommandCache;
import com.vabrant.console.DebugLogger;
import com.vabrant.console.commandextension.DefaultCommandCache;
import com.vabrant.console.commandextension.StaticReference;
import com.vabrant.console.commandextension.annotation.ConsoleCommand;
import com.vabrant.console.commandextension.annotation.ConsoleReference;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

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
		CommandCache cache = new DefaultCommandCache();
		cache.getLogger().setLevel(DebugLogger.DEBUG);

		// Add a new Object
		cache.addReference(testClass, name);

		assertTrue(cache.hasReference(name));
		assertTrue(cache.hasReference(testClass));

		// Add the same object but with a different name.
		assertThrows(ConsoleRuntimeException.class, () -> cache.addReference(testClass, "Green"));

		// Add a different object but with the same name.
		assertThrows(ConsoleRuntimeException.class, () -> cache.addReference(new TestClass(), name));
	}

	@Test
	void AddStaticReferenceTest () {
		printTestHeader("Add Static Reference Test");

		final String name = "Utils";
		CommandCache cache = new DefaultCommandCache();
		cache.getLogger().setLevel(DebugLogger.DEBUG);
		cache.addReference(TestClass.class, name);

		assertTrue(cache.hasReference(name));
		assertTrue(cache.hasReference(TestClass.class));
		assertTrue(cache.getReference(name) instanceof StaticReference);

		// Add the same class but different name
		assertThrows(RuntimeException.class, () -> cache.addReference(TestClass.class, "bob"));

		// Add a different class but same name
		assertThrows(RuntimeException.class, () -> cache.addReference(String.class, name));
	}

	@Test
	void AddInstanceMethodTest () {
		printTestHeader("Add Instance Method Test");

		CommandCache cache = new DefaultCommandCache();
		cache.getLogger().setLevel(DebugLogger.DEBUG);

		ClassReference<?> ref = cache.addReference(testClass, "test");
		cache.addCommand(ref, "print");
		cache.addCommand(ref, "print", String.class);

		assertTrue(cache.hasCommandWithName(null, "print"));
		assertTrue(cache.hasCommand(null, "print", String.class));
		assertTrue(cache.hasCommand(null, "print"));
		assertTrue(cache.hasCommand(ref, "print", String.class));
		assertTrue(cache.hasCommand(ref, "print", null));
	}

	@Test
	void AddStaticMethodTest () {
		printTestHeader("Add Static Method Test");

		CommandCache cache = new DefaultCommandCache();
		cache.getLogger().setLevel(DebugLogger.DEBUG);

		ClassReference<?> ref = cache.addReference(TestClass.class, "TestClass");

		cache.addCommand(ref, "global");

		assertTrue(cache.hasReference("TestClass"));
		assertTrue(cache.hasCommand(ref, "global", null));

		// Try to add a non static method
		assertThrows(ConsoleRuntimeException.class, () -> cache.addCommand(ref, "print"));
	}

	@Test
	void AddTestAnnotations () {
		printTestHeader("Add Test Annotations");

		CommandCache cache = new DefaultCommandCache();
		cache.getLogger().setLevel(DebugLogger.DEBUG);
		ClassReference<?> ref = cache.addReference(testClass, "test");
		cache.addAll(ref);

		assertTrue(cache.hasReference("test"));
		assertTrue(cache.hasReference("name"));
		assertTrue(cache.hasReference("red"));
		assertTrue(cache.hasCommandWithName(ref, "hello"));
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
