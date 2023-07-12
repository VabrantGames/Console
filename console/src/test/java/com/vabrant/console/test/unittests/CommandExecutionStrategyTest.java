
package com.vabrant.console.test.unittests;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.utils.Logger;
import com.vabrant.console.CommandExecutionData;
import com.vabrant.console.ConsoleCache;
import com.vabrant.console.annotation.ConsoleMethod;
import com.vabrant.console.annotation.ConsoleObject;
import com.vabrant.console.CommandExecutionStrategy;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CommandExecutionStrategyTest {

	private static Application application;

	@BeforeAll
	public static void init () {
		application = new HeadlessApplication(new ApplicationAdapter() {});
		Gdx.app.setLogLevel(Application.LOG_DEBUG);
	}

	@Test
	void basic () {
		TestClass c = new TestClass();

		ConsoleCache cache = new ConsoleCache();
		cache.setLogLevel(Logger.DEBUG);
		cache.add(new TestClass(), "test");
		cache.add(new Person("John"), "p1");
		CommandExecutionData data = new CommandExecutionData();
		data.getSettings().setDebugExecutionStrategy(true);
		data.setConsoleCache(cache);
		CommandExecutionStrategy strategy = new CommandExecutionStrategy();
// strategy.setData(data);
		strategy.init(data);

		try {
			assertTrue(strategy.execute("test.printName"));
			assertTrue(strategy.execute("test.hello"));
			assertTrue(strategy.execute("test.printAge 28"));
			assertTrue(strategy.execute("test.printLong 8929l"));
			assertTrue(strategy.execute("test.printFloat 252.0f"));
			assertTrue(strategy.execute("test.printDouble 0.8492d"));
			assertTrue(strategy.execute("test.printStats 55 0.89f .8983d 09847L"));
			assertTrue(strategy.execute("test.setAge p1 28"));
			assertTrue(strategy.execute("test.greetPerson p1"));
			assertTrue(strategy.execute("test.print \"Hello World\""));
			assertTrue(strategy.execute("test.printBoolean false"));
			assertTrue(strategy.execute("test.printBoolean2 false"));
			assertTrue(strategy.execute("test.printInt add(10 10)"));
			assertTrue(strategy.execute("test.printInt add(10 , 10)"));

			assertFalse(strategy.execute("hello()"));

			// Too many '('
			assertFalse(strategy.execute("hello(()"));

			// Too many ')'
			assertFalse(strategy.execute("hello())"));

			// Using a method that returns void as an argument. void hello()
			assertFalse(strategy.execute("hello .hello"));
		} catch (Exception e) {
// e.printStackTrace();
			System.out.println("Error: " + e.getMessage());
		}
	}

	@ConsoleObject
	static class Person {
		final String name;
		int age;

		Person (String name) {
			this.name = name;
		}

	}

	@ConsoleObject
	static class TestClass {

		@ConsoleMethod
		public void greetPerson (Person person) {
			System.out.println("Greetings " + person.name + " who is " + person.age);
		}

		@ConsoleMethod
		public void setAge (Person person, int age) {
			person.age = age;
		}

		@ConsoleMethod
		public static void print (String str) {
			System.out.println(str);
		}

		@ConsoleMethod
		public static void printName () {
			System.out.println("John");
		}

		@ConsoleMethod
		public void hello () {
			System.out.println("Hello");
		}

		@ConsoleMethod
		public void printAge (int age) {
			System.out.println("My age is " + age);
		}

		@ConsoleMethod
		public void printInt (int i) {
			System.out.println("int: " + i);
		}

		@ConsoleMethod
		public void printLong (long l) {
			System.out.println("long: " + l);
		}

		@ConsoleMethod
		public void printFloat (float f) {
			System.out.println("float: " + f);
		}

		@ConsoleMethod
		public void printDouble (double d) {
			System.out.println("double: " + d);
		}

		@ConsoleMethod
		public void printStats (int i1, float f1, double d1, long l1) {
			System.out.println("*Stats*");
			System.out.println("\tint: " + i1);
			System.out.println("\tfloat: " + f1);
			System.out.println("\tdouble: " + d1);
			System.out.println("\tlong: " + l1);
		}

		@ConsoleMethod
		public void printBoolean (boolean b) {
			System.out.println("boolean: " + b);
		}

		@ConsoleMethod
		public void printBoolean2 (Boolean b) {
			System.out.println("boolean: " + b);
		}

		@ConsoleMethod
		public int add (int x1, int x2) {
			return x1 + x2;
		}
	}
}
