package com.vabrant.console.test.unittests;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Logger;
import com.vabrant.console.ConsoleCache;
import com.vabrant.console.DebugLogger;
import com.vabrant.console.annotation.ConsoleMethod;
import com.vabrant.console.annotation.ConsoleObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ConsoleCacheTests {

	private static TestClass testClass;
	private static Application application;
	
	@BeforeAll
	public static void init() {
		application = new HeadlessApplication(new ApplicationAdapter() {});
		testClass = new TestClass();
		Gdx.app.setLogLevel(Application.LOG_DEBUG);
	}
	
	public static void printTestHeader(String name) {
		System.out.println();
    	System.out.println("//----------//" + ' ' + name + ' ' + "//----------//");
	}

	@Test
	void AddInstanceReferenceTest() {
		printTestHeader("Add Instance Reference Test");
		
		final String name = "Bob";
		ConsoleCache cache = new ConsoleCache();
		cache.setLogLevel(DebugLogger.DEBUG);

		//Add a new Object
		cache.addReference(testClass, name);

		assertEquals(cache.getReference(name).getReference(), testClass);
		assertTrue(cache.hasInstanceReference(testClass));
		assertTrue(cache.hasInstanceReference(name));
		assertEquals(cache.getInstanceReference(testClass).getReference(), testClass);
		assertEquals(cache.getInstanceReference(name).getReference(), testClass);
		
		//Add the same object but with a different name. Should log a conflict message.
		cache.addReference(testClass, "Green");
		
		//Add a different object but with the same name. Should log a conflict message.
		cache.addReference(new TestClass(), name);
	}
	
	@Test
	void AddStaticReferenceTest() {
		printTestHeader("Add Static Reference Test");
		
		final String name = "Utils";
		ConsoleCache cache = new ConsoleCache();
		cache.setLogLevel(Logger.DEBUG);
		cache.addReference(TestClass.class, name);
		
		assertTrue(cache.hasStaticReference(name));
		assertTrue(cache.hasStaticReference(TestClass.class));
		assertEquals(cache.getStaticReference(name).getReference(), TestClass.class);
		assertEquals(cache.getStaticReference(TestClass.class).getReference(), TestClass.class);
		
		//Should not be added since a class reference for TestClass was already added
		cache.addReference(TestClass.class, "bob");
		cache.addReference(String.class, name);
	}
	
	@Test
	void AddInstanceMethodTest() {
		printTestHeader("Add Instance Method Test");
		
		ConsoleCache cache = new ConsoleCache();
		cache.setLogLevel(Logger.DEBUG);
		TestClass c = new TestClass();
		
		cache.addReference(c, "test");
		cache.addMethod(c, "print");
		cache.addMethod(c, "print", String.class);

		assertTrue(cache.hasMethodWithName("print"));
		assertTrue(cache.hasMethod("print", String.class));
		assertTrue(cache.hasMethod("test", "print"));
	}

	@Test
	void AddStaticMethodTest() {
		printTestHeader("Add Static Method Test");
		
		ConsoleCache cache = new ConsoleCache();
		cache.setLogLevel(Logger.DEBUG);
		cache.addMethod(TestClass.class, "global");
		
		assertTrue(cache.hasStaticReference("TestClass"));
		assertTrue(cache.hasMethod("TestClass", "global"));
	}

	@Test
	void AddTestAnnotations() {
		printTestHeader("Add Test Annotations");
		
		ConsoleCache cache = new ConsoleCache();
		cache.setLogLevel(Logger.DEBUG);
		cache.add(testClass, "test");

		assertTrue(cache.hasReference("test"));
		assertTrue(cache.hasReference("name"));
		assertTrue(cache.hasReference("red"));
		assertTrue(cache.hasMethodWithName("hello"));
	}

	@ConsoleObject("tc")
	public static class TestClass {
		public void print() {}
		public void print(String m) {}
		public static void global() {};

		@ConsoleMethod
		public void multi(String s, int i, double d) {}
		
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
