package com.vabrant.console.test.unittests;

import com.vabrant.console.CommandEngine.ClassReference;
import com.vabrant.console.CommandEngine.DefaultCommandCache;
import com.vabrant.console.CommandEngine.DefaultCommandEngine;
import com.vabrant.console.DebugLogger;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class DefaultCommandEngineTest {

	private static DefaultCommandEngine engine;

	@BeforeAll
	public static void init() {
		DebugLogger.useSysOut();
		engine = new DefaultCommandEngine(new GlobalCache());
		engine.getLogger().setLevel(DebugLogger.DEBUG);
	}

	@Test
	void StringTest() throws Exception {
		DefaultCommandCache cache = new DefaultCommandCache();
		ClassReference ref = cache.addReference("bob", new Bob());
		cache.addCommand(ref, "world");
		cache.addCommand(ref, "add", int.class, int.class);
		cache.addCommand(ref, "printPerson", String.class, int.class, boolean.class);

		assertDoesNotThrow(() -> engine.execute(cache, "world"));
		assertEquals(engine.execute(cache, "add 5 5"), 10);
		assertDoesNotThrow(() -> engine.execute(cache, "printPerson $name 29 false"));
	}

	@Test
	void ObjectsTest() throws Exception {
		DefaultCommandCache cache = new DefaultCommandCache();
		ClassReference ref = cache.addReference("bob", new Bob());
		cache.addCommand(ref, "world");
		cache.addCommand(ref, "add", int.class, int.class);
		cache.addCommand(ref, "printPerson", String.class, int.class, boolean.class);

		Object[] cmd = {"printPerson", "John", 29, false};

		assertDoesNotThrow(() -> engine.execute(cache, new Object[]{"printPerson", "John", 29, false}));
		assertEquals(engine.execute(cache, new Object[]{"add",  5, 5}), 10);
	}

	private static class GlobalCache extends DefaultCommandCache {

		GlobalCache() {
			addReference("name", "John");
		}
	}

	public static class Bob {

		public static Bob bob = new Bob();

		public void world() {
			System.out.println("World");
		}

		public int add(int v1, int v2) {
			return v1 + v2;
		}

		public void printPerson (String name, int age, boolean hasPet) {
			System.out.println(name + " " + age + " " + hasPet);
		}
	}

}
