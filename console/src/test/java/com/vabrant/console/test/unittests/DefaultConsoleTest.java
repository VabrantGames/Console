
package com.vabrant.console.test.unittests;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.vabrant.console.*;
import com.vabrant.console.commandexecutor.ClassReference;
import com.vabrant.console.commandexecutor.Command;
import com.vabrant.console.commandexecutor.DefaultCommandCache;
import com.vabrant.console.events.ConsoleExtensionChangeEvent;
import com.vabrant.console.events.EventListener;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

public class DefaultConsoleTest {

	private static DefaultConsole console;
	private static PrintExtension extension;
	private static Application application;
	private static final String PRINT_EXTENSION_ID = "print";

	@BeforeAll
	public static void init () {
		application = new HeadlessApplication(new ApplicationAdapter() {});
		console = new DefaultConsole();

		console.getGlobalCache().addCommand("printHello", new Command() {

			@Override
			public void setSuccessMessage (String successMessage) {

			}

			@Override
			public String getSuccessMessage () {
				return "";
			}

			@Override
			public Object execute (Object[] args) throws Exception {
				System.out.println("Hello World");
				return null;
			}

			@Override
			public Class getReturnType () {
				return null;
			}
		});

		extension = new PrintExtension();
		console.addExtension(extension);
	}

	@BeforeEach
	public void reset () {
		console.setActiveExtension(null);
	}

	@Test
	void SetActiveExtensionTest () {
		AtomicBoolean called = new AtomicBoolean(false);

		EventListener listener = (e) -> {
			called.set(true);
		};

		console.subscribeToEvent(ConsoleExtensionChangeEvent.class, listener);

		// Explicitly set the extension
		console.setActiveExtension(extension);

		assertTrue(called.get());
		assertEquals(extension, console.getActiveExtension());

		called.set(false);

		// Set the extension from the execute method
		console.execute("$" + PRINT_EXTENSION_ID);

		assertTrue(called.get());
		assertEquals(extension, console.getActiveExtension());

		console.unsubscribeFromEvent(ConsoleExtensionChangeEvent.class, listener);
	}

	@Test
	void NoExtensionGlobalCacheCommandExecuteTest () {
		assertFalse(console.execute("printHel"));
		assertTrue(console.execute("printHello"));
	}

	@Test
	void ExtensionStringTest () {
		// Set active extension
		assertTrue(console.execute("$" + PRINT_EXTENSION_ID));

		// Pass input to active extension
		assertTrue(console.execute("print \"Hello World\""));

		console.setActiveExtension(null);

		// Pass input to extension but don't set active extension
		assertTrue(console.execute("$" + PRINT_EXTENSION_ID + " print \"Hello World\""));
	}

	@Test
	void ExtensionObjectTest () {
		// Pass input to extension but don't set active extension
		assertTrue(console.execute(new Object[] {"$" + PRINT_EXTENSION_ID, "print", new String[] {"World", "Hello", "John"}}));

		// Set active extension
		assertTrue(console.execute(new Object[] {extension}));

		assertTrue(console.execute(new Object[] {"print", new String[] {"Hello", "World"}}));
		assertTrue(console.execute(new Object[] {extension, "print", "Hello World"}));
	}

	static class PrintExtension extends ConsoleExtension {

		PrintExtension () {
			super(PRINT_EXTENSION_ID);

			DefaultCommandCache cache = (DefaultCommandCache)commandCache;
			cache.setLogLevel(DebugLogger.DEBUG);

			ClassReference ref = cache.addReference("this", this);
			cache.addMethodCommand(ref, "print", String.class);
			cache.addMethodCommand(ref, "print", String[].class);
		}

		public void print (String str) {
			System.out.println("Print: " + str);
		}

		public void print (String[] arr) {
			for (String s : arr) {
				System.out.println("Print: " + s);
			}
		}

	}
}
