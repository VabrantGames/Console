
package com.vabrant.console.test.unittests;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.utils.Logger;
import com.vabrant.console.ConsoleCache;
import com.vabrant.console.annotation.ConsoleMethod;
import com.vabrant.console.annotation.ConsoleObject;
import com.vabrant.console.executionstrategy.ExecutionStrategyInput;
import com.vabrant.console.executionstrategy.SimpleExecutionStrategy;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SimpleExecutionStrategyTest {

	private static Application application;

	@BeforeAll
	public static void init () {
		application = new HeadlessApplication(new ApplicationAdapter() {});
		Gdx.app.setLogLevel(Application.LOG_DEBUG);
	}

	@Test
	void basic () {
		TestClass c = new TestClass();

		SimpleExecutionStrategy strategy = new SimpleExecutionStrategy();
		ExecutionStrategyInput input = new ExecutionStrategyInput();
		ConsoleCache cache = new ConsoleCache();
		cache.setLogLevel(Logger.DEBUG);
		cache.add(new TestClass(), "test");
		cache.add(new Person("John"), "p1");
		input.setConsoleCache(cache);

		try {
			assertDoesNotThrow( () -> strategy.execute(input.setText("test.printName")));
			assertDoesNotThrow( () -> strategy.execute(input.setText("test.hello")));
			assertDoesNotThrow( () -> strategy.execute(input.setText("test.printAge 28")));
			assertDoesNotThrow( () -> strategy.execute(input.setText("test.printLong 8929l")));
			assertDoesNotThrow( () -> strategy.execute(input.setText("test.printFloat 252.0f")));
			assertDoesNotThrow( () -> strategy.execute(input.setText("test.printDouble 0.8492d")));
			assertDoesNotThrow( () -> strategy.execute(input.setText("test.printStats 55 0.89f .8983d 09847L")));
			assertDoesNotThrow( () -> strategy.execute(input.setText("test.setAge p1 28")));
			assertDoesNotThrow( () -> strategy.execute(input.setText("test.greetPerson p1")));
			assertDoesNotThrow( () -> strategy.execute(input.setText("test.print \"Hello World\"")));
			assertDoesNotThrow( () -> strategy.execute(input.setText("test.printBoolean false")));
			assertDoesNotThrow( () -> strategy.execute(input.setText("test.printBoolean2 false")));
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
	}
}
