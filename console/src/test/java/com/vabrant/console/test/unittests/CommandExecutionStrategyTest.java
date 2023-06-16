
package com.vabrant.console.test.unittests;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.utils.Logger;
import com.vabrant.console.ConsoleCache;
import com.vabrant.console.annotation.ConsoleMethod;
import com.vabrant.console.annotation.ConsoleObject;
import com.vabrant.console.executionstrategy.CommandExecutionStrategy;
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
		CommandExecutionStrategy strategy = new CommandExecutionStrategy(true);
		strategy.setConsoleCache(cache);
		cache.setLogLevel(Logger.DEBUG);
		cache.add(new TestClass(), "test");
		cache.add(new Person("John"), "p1");

		try {
			assertDoesNotThrow( () -> strategy.execute("test.printName"));
			assertDoesNotThrow( () -> strategy.execute("test.hello"));
			assertDoesNotThrow( () -> strategy.execute("test.printAge 28"));
			assertDoesNotThrow( () -> strategy.execute("test.printLong 8929l"));
			assertDoesNotThrow( () -> strategy.execute("test.printFloat 252.0f"));
			assertDoesNotThrow( () -> strategy.execute("test.printDouble 0.8492d"));
			assertDoesNotThrow( () -> strategy.execute("test.printStats 55 0.89f .8983d 09847L"));
			assertDoesNotThrow( () -> strategy.execute("test.setAge p1 28"));
			assertDoesNotThrow( () -> strategy.execute("test.greetPerson p1"));
			assertDoesNotThrow( () -> strategy.execute("test.print \"Hello World\""));
			assertDoesNotThrow( () -> strategy.execute("test.printBoolean false"));
			assertDoesNotThrow( () -> strategy.execute("test.printBoolean2 false"));
			assertDoesNotThrow( () -> strategy.execute("test.printInt add(10 10)"));
			assertDoesNotThrow( () -> strategy.execute("test.printInt add(10 , 10)"));

			assertThrows(RuntimeException.class, () -> strategy.execute("hello()"));

			// Too many '('
			assertThrows(RuntimeException.class, () -> strategy.execute("hello(()"));

			// Too many ')'
			assertThrows(RuntimeException.class, () -> strategy.execute("hello())"));

			// Using a method that returns void as an argument. void hello()
			assertThrows(RuntimeException.class, () -> strategy.execute("hello .hello"));
		} catch (Exception e) {
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
