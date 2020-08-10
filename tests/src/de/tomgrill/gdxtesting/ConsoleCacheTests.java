package de.tomgrill.gdxtesting;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.Method;
import com.vabrant.console.ConsoleCache;
import com.vabrant.console.DebugLogger;
import com.vabrant.console.annotation.ConsoleMethod;
import com.vabrant.console.annotation.ConsoleObject;

@RunWith(GdxTestRunner.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ConsoleCacheTests {
	
	@BeforeClass
	public static void init() {
		DebugLogger.useSysOut();
	}
	
	public static void printTestHeader(String name) {
		System.out.println();
    	System.out.println("//----------//" + ' ' + name + ' ' + "//----------//");
	}

	@Test
	public void AddInstanceReferenceTest() {
		printTestHeader("Add Instance Reference Test");
		
		final String name = "Bob";
		ConsoleCache cache = new ConsoleCache();
		TestClass testClass = new TestClass();
		
		//Add a new Object
		cache.addReference(testClass, name);
		
		assertTrue(cache.hasInstanceReference(testClass));
		assertTrue(cache.hasInstanceReference(name));
		assertNotNull(cache.getInstanceReference(testClass));
		assertNotNull(cache.getInstanceReference(name));
		
		//Add the same object but with a different name. Should log a conflict message.
		cache.addReference(testClass, "Green");
		
		//Add a different object but with the same name. Should log a conflict message.
		cache.addReference(new TestClass(), name);
	}
	
	@Test
	public void AddStaticReferenceTest() {
		printTestHeader("Add Static Reference Test");
		
		final String name = "Utils";
		ConsoleCache cache = new ConsoleCache();
		
		cache.addReference(TestClass.class, name);
		
		assertTrue(cache.hasStaticReference(name));
		assertTrue(cache.hasStaticReference(TestClass.class));
		assertNotNull(cache.getStaticReference(name));
		assertNotNull(cache.getStaticReference(TestClass.class));
		
		//Should not be added since a class reference for TestClass was already added
		cache.addReference(TestClass.class, "bob");
		cache.addReference(String.class, name);
	}
	
	@Test 
	public void AddInstanceMethodTest() {
		printTestHeader("Add Instance Method Test");
		
		ConsoleCache cache = new ConsoleCache();
		TestClass c = new TestClass();
		
		cache.addReference(c, "test");
		cache.addMethod(c, "print");
		cache.addMethod(c, "print", String.class);
		
//		assertTrue(cache.hasMethod("print"));
//		
//		//0 args
//		assertTrue(cache.hasMethod("test", "print", null));
//		
//		
//		assertTrue(cache.hasMethod("test", "print", String.class));
	}

	@Test
	public void AddStaticMethodTest() {
		printTestHeader("Add Static Method Test");
		
		ConsoleCache cache = new ConsoleCache();
		
		cache.addMethod(TestClass.class, "global");
		
		assertTrue(cache.hasStaticReference("TestClass"));
		assertTrue(cache.hasMethod("TestClass", "global"));
	}
	
	@Test
	public void AddTestAnnotations() {
		printTestHeader("Add Test Annotations");
		
		ConsoleCache cache = new ConsoleCache();
		TestClass testClass = new TestClass();
		
		cache.add(testClass, "test");
	}

	@ConsoleObject("tc")
	public static class TestClass {
		public void print() {}
		public void print(String m) {}
		public static void global() {};
		
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
