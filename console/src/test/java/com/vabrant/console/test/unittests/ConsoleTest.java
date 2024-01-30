
package com.vabrant.console.test.unittests;

import com.vabrant.console.Console;
import com.vabrant.console.ConsoleExtension;
import com.vabrant.console.DefaultConsole;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ConsoleTest {

	@Test
	void StrategyTest () {
		Console console = new DefaultConsole();
		console.addExtension(new PrintExtension());

		assertFalse(console.execute(""));
		assertFalse(console.execute("/"));

		// Pass input to active extension
		assertFalse(console.execute("Hello World"));

		// Sets active extension
		assertTrue(console.execute("/test"));

		// Pass input to "/test" but don't set active extension
		assertTrue(console.execute("/test Hello World"));

		// Empty string is given to active extension
		assertTrue(console.execute(""));

		console.setActiveExtension(null);

		assertFalse(console.execute(new String[] {}));
		assertFalse(console.execute(new String[] {"Hello", "World"}));
		assertTrue(console.execute(new String[] {"/test", "World", "Hello", "John"}));

		assertTrue(console.execute(new String[] {"/test", "World", "Hello", "John"}));
	}

	@Test
	void SpecificExtensionTest () {
		Console console = new DefaultConsole();
		PrintExtension extension = new PrintExtension();

		assertTrue(console.execute(extension, "Hello World"));
	}

	static class PrintExtension extends ConsoleExtension {

		PrintExtension () {
			super("test");
		}

		@Override
		public Boolean execute (Object o) throws Exception {
			if (o instanceof String) {
				System.out.println("PrintExtension: " + (String)o);
				return true;
			} else if (o instanceof String[]) {
				String[] arr = (String[])o;
				for (String s : arr) {
					System.out.println("PrintExtension: " + s);
				}
				return true;
			}
			return false;
		}
	}
}
