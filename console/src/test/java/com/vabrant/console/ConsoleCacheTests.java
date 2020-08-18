package com.vabrant.console;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.badlogic.gdx.graphics.Color;
import com.vabrant.console.annotation.ConsoleMethod;
import com.vabrant.console.annotation.ConsoleObject;

public class ConsoleCacheTests {
	
	@BeforeAll
	public static void init() {
		DebugLogger.useSysOut();
	}
	
	public static void printTestHeader(String name) {
		System.out.println();
    	System.out.println("//----------//" + ' ' + name + ' ' + "//----------//");
	}

	@Test
	public void AddReferenceTest() {
		printTestHeader("Add Instance Reference Test");
		
		final String instanceName = "inst";
		final String staticName = "static";
		ConsoleCache cache = new ConsoleCache();
		cache.setLogLevel(DebugLogger.DEBUG);
		TestClass testClass = new TestClass();
		
		//Add an instance reference
		cache.addReference(testClass, instanceName);
		
		//Add a static reference
		cache.addReference(TestClass.class, staticName);
		
		assertTrue(cache.hasReference(testClass));
		assertTrue(cache.hasReference(instanceName));
		assertNotNull(cache.getReference(testClass));
		assertNotNull(cache.getReference(instanceName));
		
		assertTrue(cache.hasReference(TestClass.class));
		assertTrue(cache.hasReference(staticName));
		assertNotNull(cache.getReference(TestClass.class));
		assertNotNull(cache.getReference(instanceName));
		
		
		//Add the same object but with a different name. Should log a conflict message.
		cache.addReference(testClass, "Green");
		
		//Add a different object but with the same name. Should log a conflict message.
		cache.addReference(new TestClass(), instanceName);
	}

	@Test 
	public void AddMethodTest() {
		printTestHeader("Add Method Test");
		
		ConsoleCache cache = new ConsoleCache();
		cache.setLogLevel(DebugLogger.DEBUG);
		TestClass c = new TestClass();
		
		cache.addReference(c, "instance");
		cache.addMethod(c, "print");
		cache.addMethod(c, "print", String.class);
		assertTrue(cache.hasMethod("print"));
		assertTrue(cache.hasMethod("instance", "print", new Class[0]));
		assertTrue(cache.hasMethod("instance", "print", String.class));
		
		cache.addReference(TestClass.class, "static");
		cache.addMethod(TestClass.class, "global");
		assertTrue(cache.hasMethod("global"));
		assertTrue(cache.hasMethod("static", "global"));
	}

	@Test
	public void AddTestAnnotations() {
		printTestHeader("Add Test Annotations");
		
		ConsoleCache cache = new ConsoleCache();
		cache.setLogLevel(DebugLogger.DEBUG);
		TestClass testClass = new TestClass();
		
		cache.add(testClass, "test");
	}

	@ConsoleObject("tc")
	private static class TestClass {
		public void print() {}
		public void print(String m) {}
		public static void global() {}
		
		@ConsoleObject("name")
		public final String name = "console";
		
		@ConsoleObject("red")
		public final Color color = new Color(1, 0, 0, 1);
		
		@ConsoleMethod
		public void hello(String name) {}
		
		@ConsoleMethod
		public void changeColor(Color color) {}
	}
	
}
