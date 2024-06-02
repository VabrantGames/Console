
package com.vabrant.console.test.unittests;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.vabrant.console.*;
import com.vabrant.console.CommandEngine.ClassReference;
import com.vabrant.console.CommandEngine.DefaultCommandCache;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ConsoleTest {

	private static DefaultConsole console;
	private static PrintExtension extension;
	private static Application application;

	@BeforeAll
	public static void init () {
		application = new HeadlessApplication(new ApplicationAdapter() {});
		console = new DefaultConsole();
		extension = new PrintExtension();
		console.addExtension(extension);
	}

	@BeforeEach
	public void reset () {
		console.setActiveExtension(null);
	}

	@Test
	void StringExtensionInputTest () {
		// Pass input to active extension. No active extension is set
		assertFalse(console.execute(""));
		assertFalse(console.execute("$"));
		assertFalse(console.execute("print \"Hello World\""));

		// Set active extension
		assertTrue(console.execute("$test"));
		assertNotNull(console.getActiveExtension());

		assertTrue(console.execute("print \"Hello World\""));

		console.setActiveExtension(null);

		// Pass input to extension but don't set active extension
		assertTrue(console.execute("$test print \"Hello World\""));
	}

	@Test
	void ObjectExtensionInputTest () {
		assertFalse(console.execute(new String[] {}));
		assertFalse(console.execute(new Object[] {"print", new String[] {"Hello", "World"}}));
		assertTrue(console.execute(new Object[] {"$test", "print", new String[] {"World", "Hello", "John"}}));

		assertTrue(console.execute(new Object[] {extension}));

		assertTrue(console.execute(new Object[] {"print", new String[] {"Hello", "World"}}));

		assertTrue(console.execute(new Object[] {extension, "print", "Hello World"}));
	}

	@Test
	void SpecificExtensionTest () {
		Console console = new DefaultConsole();
		PrintExtension extension = new PrintExtension();
		console.addExtension(extension);

		assertTrue(console.execute(extension, "print \"Hello World\""));
	}

	@Test
	void SystemCommandsTest () {
		DefaultCommandCache cache = new DefaultCommandCache();
		cache.addCommand(new Print(), "print", String.class);

		DefaultConsoleConfiguration config = new DefaultConsoleConfiguration();
		config.setGlobalCommandCache(cache);

		Console console = new DefaultConsole(config);

		assertTrue(console.execute("/print \"Hello World\""));
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
			cache.getLogger().setLevel(DebugLogger.DEBUG);

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
			return console.getCommandEngine().execute(cache, o).getExecutionStatus();
		}
	}
}
