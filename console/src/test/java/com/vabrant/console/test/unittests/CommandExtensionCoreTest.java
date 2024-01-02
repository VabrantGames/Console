
package com.vabrant.console.test.unittests;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.utils.Logger;
import com.vabrant.console.DebugLogger;
import com.vabrant.console.commandextension.*;
import com.vabrant.console.commandextension.annotation.ConsoleCommand;
import com.vabrant.console.commandextension.annotation.ConsoleReference;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CommandExtensionCoreTest {

	private static Application application;

	@BeforeAll
	public static void init () {
		application = new HeadlessApplication(new ApplicationAdapter() {});
		Gdx.app.setLogLevel(Application.LOG_DEBUG);
	}

	@Test
	void basic () {
		TestClass c = new TestClass();

		CommandCache cache = new DefaultCommandCache();
		cache.getLogger().setLevel(Logger.DEBUG);

		ClassReference<?> classRef = cache.addReference(new TestClass(), "test");
		ClassReference<?> personRef = cache.addReference(new Person("John"), "p1");

		cache.addAll(classRef);
		cache.addAll(personRef);
		CommandData data = new CommandData();
		data.setConsoleCache(cache);
		CommandExtensionCore strategy = new CommandExtensionCore(null);
		strategy.getLogger().setLevel(DebugLogger.DEBUG);
//		strategy.init(data);

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

	@ConsoleReference
	static class Person {
		final String name;
		int age;

		Person (String name) {
			this.name = name;
		}

	}

	@ConsoleReference
	static class TestClass {

		@ConsoleCommand
		public void greetPerson (Person person) {
			System.out.println("Greetings " + person.name + " who is " + person.age);
		}

		@ConsoleCommand
		public void setAge (Person person, int age) {
			person.age = age;
		}

		@ConsoleCommand
		public static void print (String str) {
			System.out.println(str);
		}

		@ConsoleCommand
		public static void printName () {
			System.out.println("John");
		}

		@ConsoleCommand
		public void hello () {
			System.out.println("Hello");
		}

		@ConsoleCommand
		public void printAge (int age) {
			System.out.println("My age is " + age);
		}

		@ConsoleCommand
		public void printInt (int i) {
			System.out.println("int: " + i);
		}

		@ConsoleCommand
		public void printLong (long l) {
			System.out.println("long: " + l);
		}

		@ConsoleCommand
		public void printFloat (float f) {
			System.out.println("float: " + f);
		}

		@ConsoleCommand
		public void printDouble (double d) {
			System.out.println("double: " + d);
		}

		@ConsoleCommand
		public void printStats (int i1, float f1, double d1, long l1) {
			System.out.println("*Stats*");
			System.out.println("\tint: " + i1);
			System.out.println("\tfloat: " + f1);
			System.out.println("\tdouble: " + d1);
			System.out.println("\tlong: " + l1);
		}

		@ConsoleCommand
		public void printBoolean (boolean b) {
			System.out.println("boolean: " + b);
		}

		@ConsoleCommand
		public void printBoolean2 (Boolean b) {
			System.out.println("boolean: " + b);
		}

		@ConsoleCommand
		public int add (int x1, int x2) {
			return x1 + x2;
		}
	}
}
