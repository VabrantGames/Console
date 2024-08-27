
package com.vabrant.console.test.unittests;

import com.vabrant.console.CommandEngine.ClassReference;
import com.vabrant.console.CommandEngine.Command;
import com.vabrant.console.CommandEngine.DefaultCommandCache;
import com.vabrant.console.CommandEngine.DefaultCommandExecutor;
import com.vabrant.console.DebugLogger;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class DefaultCommandExecutorTest {

	private static DefaultCommandExecutor engine;

	@BeforeAll
	public static void init () {
		DebugLogger.useSysOut();
		engine = new DefaultCommandExecutor(null, new GlobalCache());
		engine.getLogger().setLevel(DebugLogger.DEBUG);
	}

	@Test
	void StringTest () throws Exception {
		DefaultCommandCache cache = new DefaultCommandCache();
		ClassReference ref = cache.addReference("bob", new Bob());
		cache.addCommand(ref, "world");
		cache.addCommand(ref, "add", int.class, int.class);
		cache.addCommand(ref, "printPerson", String.class, int.class, boolean.class);

		assertTrue(engine.execute(cache, "world").getExecutionStatus());
		assertEquals(10, engine.execute(cache, "add 5 5").getResult());
		assertTrue(engine.execute(cache, "printPerson $name 29 false").getExecutionStatus());
	}

	@Test
	void ObjectsTest () throws Exception {
		DefaultCommandCache cache = new DefaultCommandCache();
		ClassReference ref = cache.addReference("bob", new Bob());
		cache.addCommand(ref, "world");
		cache.addCommand(ref, "add", int.class, int.class);
		cache.addCommand(ref, "printPerson", String.class, int.class, boolean.class);

		Object[] cmd = {"printPerson", "John", 29, false};

		assertTrue(engine.execute(cache, new Object[] {"printPerson", "John", 29, false}).getExecutionStatus());
		assertEquals(10, engine.execute(cache, new Object[] {"add", 5, 5}).getResult());
	}

	@Test
	void NoArgTest () {
		DefaultCommandCache cache = new DefaultCommandCache();
		ClassReference bobRef = cache.addReference("bob", new Bob());
		cache.addCommand(bobRef, "noArg");

		// Should default to the method command when for "noArg" when no custom command for "noArg" is added
		assertEquals(0, engine.execute(cache, "noArg").getResult());

		cache.addCommand("noArg", new CustomCommand());

		assertEquals(1, engine.execute(cache, "noArg").getResult());
		assertEquals(0, engine.execute(cache, "bob.noArg").getResult());
	}

	private static class GlobalCache extends DefaultCommandCache {

		GlobalCache () {
			addReference("name", "John");
		}
	}

	public static class CustomCommand implements Command {

		@Override
		public void setSuccessMessage (String successMessage) {

		}

		@Override
		public String getSuccessMessage () {
			return null;
		}

		@Override
		public Object execute (Object[] args) throws Exception {
			return 1;
		}

		@Override
		public Class getReturnType () {
			return int.class;
		}
	}

	public static class Bob {

		public static Bob bob = new Bob();

		public int noArg () {
			return 0;
		}

		public void world () {
			System.out.println("World");
		}

		public int add (int v1, int v2) {
			return v1 + v2;
		}

		public void printPerson (String name, int age, boolean hasPet) {
			System.out.println(name + " " + age + " " + hasPet);
		}
	}

}
