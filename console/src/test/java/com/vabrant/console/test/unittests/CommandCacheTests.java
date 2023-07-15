
package com.vabrant.console.test.unittests;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.graphics.Color;
import com.vabrant.console.commandextension.CommandCache;
import com.vabrant.console.DebugLogger;
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
		CommandCache cache = new CommandCache();
		cache.getLogger().setLevel(DebugLogger.DEBUG);

		// Add a new Object
		cache.addReference(testClass, name);

		assertEquals(cache.getReference(name).getReference(), testClass);
		assertTrue(cache.hasInstanceReference(testClass));
		assertTrue(cache.hasInstanceReference(name));
		assertEquals(cache.getInstanceReference(testClass).getReference(), testClass);
		assertEquals(cache.getInstanceReference(name).getReference(), testClass);

		// Add the same object but with a different name. Should log a conflict message.
		cache.addReference(testClass, "Green");

		// Add a different object but with the same name. Should log a conflict message.
		cache.addReference(new TestClass(), name);
	}

	@Test
	void AddStaticReferenceTest () {
		printTestHeader("Add Static Reference Test");

		final String name = "Utils";
		CommandCache cache = new CommandCache();
		cache.getLogger().setLevel(DebugLogger.DEBUG);
		cache.addReference(TestClass.class, name);

		assertTrue(cache.hasStaticReference(name));
		assertTrue(cache.hasStaticReference(TestClass.class));
		assertEquals(cache.getStaticReference(name).getReference(), TestClass.class);
		assertEquals(cache.getStaticReference(TestClass.class).getReference(), TestClass.class);

		// Should not be added since a class reference for TestClass was already added
		cache.addReference(TestClass.class, "bob");
		cache.addReference(String.class, name);
	}

	@Test
	void AddInstanceMethodTest () {
		printTestHeader("Add Instance Method Test");

		CommandCache cache = new CommandCache();
		cache.getLogger().setLevel(DebugLogger.DEBUG);
		TestClass c = new TestClass();

		cache.addReference(c, "test");
		cache.addCommand(c, "print");
		cache.addCommand(c, "print", String.class);

		assertTrue(cache.hasCommandWithName("print"));
		assertTrue(cache.hasCommand("print", String.class));
		assertTrue(cache.hasCommand("test", "print"));
	}

	@Test
	void AddStaticMethodTest () {
		printTestHeader("Add Static Method Test");

		CommandCache cache = new CommandCache();
		cache.getLogger().setLevel(DebugLogger.DEBUG);
		cache.addCommand(TestClass.class, "global");

		assertTrue(cache.hasStaticReference("TestClass"));
		assertTrue(cache.hasCommand("TestClass", "global"));
	}

	@Test
	void AddTestAnnotations () {
		printTestHeader("Add Test Annotations");

		CommandCache cache = new CommandCache();
		cache.getLogger().setLevel(DebugLogger.DEBUG);
		cache.addAll(testClass, "test");

		assertTrue(cache.hasReference("test"));
		assertTrue(cache.hasReference("name"));
		assertTrue(cache.hasReference("red"));
		assertTrue(cache.hasCommandWithName("hello"));
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
