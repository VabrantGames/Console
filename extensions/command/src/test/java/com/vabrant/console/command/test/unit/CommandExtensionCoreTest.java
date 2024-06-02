
package com.vabrant.console.command.test.unit;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.utils.Logger;
import com.vabrant.console.CommandEngine.ConsoleCommand;
import com.vabrant.console.CommandEngine.ConsoleReference;
import commandextension.ClassReference;
import commandextension.CommandCache;
import commandextension.CommandExtension;
import commandextension.DefaultCommandCache;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CommandExtensionCoreTest {

	public static CommandExtension extension;
	private static Application application;

	@BeforeAll
	public static void init () {
		application = new HeadlessApplication(new ApplicationAdapter() {});
		extension = new CommandExtension();
		Gdx.app.setLogLevel(Application.LOG_DEBUG);
	}

	@BeforeEach
	public void reset () {
		extension.setConsoleCache(null);
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

		extension.setConsoleCache(cache);

		try {
			assertTrue(extension.execute("test.printName"));
			assertTrue(extension.execute("test.hello"));
			assertTrue(extension.execute("test.printAge 28"));
			assertTrue(extension.execute("test.printLong 8929l"));
			assertTrue(extension.execute("test.printFloat 252.0f"));
			assertTrue(extension.execute("test.printDouble 0.8492d"));
			assertTrue(extension.execute("test.printStats 55 0.89f .8983d 09847L"));
			assertTrue(extension.execute("test.setAge p1 28"));
			assertTrue(extension.execute("test.greetPerson p1"));
			assertTrue(extension.execute("test.print \"Hello World\""));
			assertTrue(extension.execute("test.printBoolean false"));
			assertTrue(extension.execute("test.printBoolean2 false"));
			assertTrue(extension.execute("test.printInt add(10 10)"));
			assertTrue(extension.execute("test.printInt add(10 , 10)"));

			assertFalse(extension.execute("hello()"));

			// Too many '('
			assertFalse(extension.execute("hello(()"));

			// Too many ')'
			assertFalse(extension.execute("hello())"));

			// Using a method that returns void as an argument. void hello()
			assertFalse(extension.execute("hello .hello"));
		} catch (Exception e) {
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
