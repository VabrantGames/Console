package com.vabrant.console.test.unittests;

import com.badlogic.gdx.Application;
import com.vabrant.console.CommandEngine.ClassReference;
import com.vabrant.console.CommandEngine.Command;
import com.vabrant.console.CommandEngine.ConsoleReference;
import com.vabrant.console.CommandEngine.advanced.AdvancedCommandCache;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AdvancedCommandCacheTest {

	private static Application application;

	@BeforeAll
	public static void init () {
//		commandCache = new AdvancedCommandCache();
	}

	@Test
	void AddReferenceTest () {
		AdvancedCommandCache commandCache = new AdvancedCommandCache();
		ClassReference ref = commandCache.addReference("ref", new Object());

		assertTrue(commandCache.hasReference("ref"));
		assertEquals(ref, commandCache.getReference("ref"));

		// Attempt to create a reference with the same ID
		assertThrows(Exception.class, () -> {
			commandCache.addReference("ref", new Object());
		});
	}

	@Test
	void AddCommandTest () {
		AdvancedCommandCache commandCache = new AdvancedCommandCache();

		Command customCommand = new Command() {

			@Override
			public void setSuccessMessage (String successMessage) {
			}

			@Override
			public String getSuccessMessage () {
				return "";
			}

			@Override
			public Object execute (Object[] args) throws Exception {
				return null;
			}

			@Override
			public Class getReturnType () {
				return null;
			}
		};

		class HelloClass {

			void hello () {
			}
		}

		commandCache.addCommand("hello", customCommand);

		ClassReference ref = commandCache.addReference("ref", new HelloClass());
		commandCache.addCommand(ref, "hello", null);

		assertEquals(customCommand, commandCache.getCommand("hello"));
		System.out.println(commandCache.getCommand("ref", "hello") == null);
		assertNotNull(commandCache.getCommand("ref", "hello"));
	}
}
