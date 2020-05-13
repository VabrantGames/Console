package de.tomgrill.gdxtesting;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.Field;
import com.vabrant.console.ConsoleCache;
import com.vabrant.console.DebugLogger;
import com.vabrant.console.annotation.ConsoleMethod;
import com.vabrant.console.annotation.ConsoleReference;

@RunWith(GdxTestRunner.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ConsoleCacheTests {
	
	@BeforeClass
	public static void init() {
		DebugLogger.usSysOut();
	}
	
	public static void printTestHeader(String name) {
		System.out.println();
    	System.out.println("//----------//" + ' ' + name + ' ' + "//----------//");
	}
	
	@Test
	public void addInstanceReferenceTest() {
		printTestHeader("Add Instance Reference Test");
		
		final String name = "Bob";
		ConsoleCache cache = new ConsoleCache();
		TestClass testClass = new TestClass();
		
		//Add a new Object
		cache.addReference(testClass, name);
		
		assertTrue(cache.hasInstanceReference(testClass));
		assertTrue(cache.hasInstanceReference(name));
		
		//Add the same object but with a different name. Should log a conflict message.
		cache.addReference(testClass, "Green");
		
		//Add a different object but with the same name. Should log a conflict message.
		cache.addReference(new TestClass(), name);
	}
	
	@Test
	public void addStaticReferenceTest() {
		printTestHeader("Add Static Reference Test");
		
		final String name = "Utils";
		ConsoleCache cache = new ConsoleCache();
		
		cache.addReference(TestClass.class, name);
		
		assertTrue(cache.hasStaticReference(name));
		assertNotNull(cache.getStaticReference(name));
		
		cache.addReference(TestClass.class, "bob");
		cache.addReference(String.class, name);
	}
	
	@Test 
	public void addInstanceMethodTest() {
		printTestHeader("Add Instance Method Test");
		
		ConsoleCache cache = new ConsoleCache();
		TestClass c = new TestClass();
		
		cache.addReference(c, "test");
		cache.addMethod(c, "print");
		cache.addMethod(c, "print", String.class);
		
		assertTrue(cache.hasMethodWithName("print"));
		assertTrue(cache.hasMethod("test", "print"));
		assertTrue(cache.hasMethod("test", "print", String.class));
	}
	
	@Test
	public void addStaticMethodTest() {
		printTestHeader("Add Static Method Test");
		
		ConsoleCache cache = new ConsoleCache();
		
		cache.addMethod(TestClass.class, "global");
		
		assertTrue(cache.hasStaticReference("TestClass"));
		assertTrue(cache.hasMethod("TestClass", "global"));
	}
	
	@Test
	public void addTestAnnotations() {
		printTestHeader("Add Test Annotations");
		
		ConsoleCache cache = new ConsoleCache();
		TestClass testClass = new TestClass();
		
		cache.add(testClass, "test");
	}
	
	@ConsoleReference("tc")
	public static class TestClass {
		public void print() {}
		public void print(String m) {}
		public static void global() {};
		
		@ConsoleReference("name")
		public final String name = "console";
		
		@ConsoleReference("red")
		public final Color color = new Color(1, 0, 0, 1);
		
		@ConsoleMethod
		public void hello(String name) {}
		
		@ConsoleMethod
		public void changeColor(Color color) {}
	}
	
}
