package de.tomgrill.gdxtesting;

import static org.junit.Assert.assertTrue;

import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import com.vabrant.console.ConsoleCache;
import com.vabrant.console.DebugLogger;
import com.vabrant.console.annotation.ConsoleObject;

@RunWith(GdxTestRunner.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ConsoleCacheTests {
	
	@BeforeClass
	public static void init() {
		DebugLogger.usSysOut();
	}
	
	@Test
	public void addObjectTest() {
		ConsoleCache cache = new ConsoleCache();
		
		final String name = "Bob";
		TestClass c = new TestClass();
		
		//Add a new Object
		cache.addObject(name, c);
		
		assertTrue(cache.hasInstanceReference(c));
		assertTrue(cache.hasInstanceReference(name));
		
		//Add the same object but with a different name. Should give an error.
		cache.addObject("Green", c);
		
		//Add a different object but with the same name. Should give an error.
		cache.addObject(name, new TestClass());
	}
	
	@Test 
	public void addMethod() {
		ConsoleCache cache = new ConsoleCache();
		
		TestClass c = new TestClass();
		
		cache.addMethod("print", c);
	}
	
	private static class TestClass {
		public static void billy() {}
		public void print() {
		}
	}

	@ConsoleObject("Console")
	private static class ConsoleTestClass {
	}
}
