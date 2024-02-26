
package com.vabrant.console.test.unittests;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.vabrant.console.CommandEngine.ClassReference;
import com.vabrant.console.CommandEngine.CommandCache;
import com.vabrant.console.CommandEngine.DefaultCommandCache;
import com.vabrant.console.Console;
import com.vabrant.console.ConsoleExtension;
import com.vabrant.console.DefaultConsole;
import com.vabrant.console.DefaultConsoleConfiguration;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ConsoleTest {

	private static Application application;

	@BeforeAll
	public static void init () {
		application = new HeadlessApplication(new ApplicationAdapter() {});
	}

	@Test
	void ExtensionTest () {
		Console console = new DefaultConsole();
		console.addExtension(new PrintExtension());

		assertFalse(console.execute(""));
		assertFalse(console.execute("$"));

		// Pass input to active extension. No active extension is set
		assertFalse(console.execute("print \"Hello World\""));

		// Sets active extension
		assertTrue(console.execute("$test"));
		assertNotNull(console.getActiveExtension());

		// Pass input to 'test' extension but don't set active extension
		assertTrue(console.execute("$test print \"Hello World\""));

		// Empty string is given to active extension
		assertFalse(console.execute(""));

		console.setActiveExtension(null);
		assertNull(console.getActiveExtension());

		assertFalse(console.execute(new String[] {}));
		assertFalse(console.execute(new Object[] {"print", new String[]{"Hello", "World"}}));
		assertTrue(console.execute(new Object[] {"$test", "print", new String[]{"World", "Hello", "John"}}));
		assertTrue(console.execute(new Object[] {"$test", "print", new String[]{"World", "Hello", "John"}}));
	}

	@Test
	void SpecificExtensionTest () {
		Console console = new DefaultConsole();
		PrintExtension extension = new PrintExtension();
		console.addExtension(extension);

		assertTrue(console.execute(extension, "print \"Hello World\""));
	}

	@Test
	void SystemCommandsTest() {
		DefaultCommandCache cache = new DefaultCommandCache();
		cache.addCommand(new Print(), "print", String.class);

		DefaultConsoleConfiguration config = new DefaultConsoleConfiguration();
		config.setGlobalCommandCache(cache);

		Console console = new DefaultConsole(config);

		assertTrue(console.execute("/print \"Hello World\""));
	}

	@Test
	void NonSpecifiedExtensionTest() {
		DefaultCommandCache cache = new DefaultCommandCache();
		Console console = new DefaultConsole();
	}

	static class Print {

		public void print (String str) {
			System.out.println("Print: " + str);
		}
	}

	static class PrintExtension extends ConsoleExtension {

		private DefaultCommandCache cache;

		PrintExtension () {
			super("test");
			cache = new DefaultCommandCache();

			ClassReference ref = cache.addReference("this", this);
			cache.addCommand(ref, "print", String.class);
			cache.addCommand(ref, "print", String[].class);
		}

		public void print (String str) {
			System.out.println("Print: " + str);
		}

		public void print (String[] arr) {
			for (String s : arr) {
				System.out.println("Print: " + s);
			}
		}

		@Override
		public Boolean execute (Object o) throws Exception {
			console.getCommandEngine().execute(cache, o);
			return true;
		}
	}
}
