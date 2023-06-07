
package com.vabrant.console.test.unittests;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.Field;
import com.badlogic.gdx.utils.reflect.Method;
import com.vabrant.console.ConsoleCache;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class MethodLookupTest {

	private static Application application;

	@BeforeAll
	public static void init () {
		application = new HeadlessApplication(new ApplicationAdapter() {});
	}

	private <T> Object executePrivateMethod (Object obj, String methodName, Class[] parameters, T... args) {
		try {
			Method m = ClassReflection.getDeclaredMethod(obj.getClass(), methodName, parameters);
			m.setAccessible(true);
			return m.invoke(obj, args);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private Method getMethod (Class<?> c, String name, Class<?>... args) {
		try {
			Method m = ClassReflection.getMethod(c, name, args);
			m.setAccessible(true);
			return m;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public ConsoleCache.MethodLookup getMethodLookup (ConsoleCache cache) {
		try {
			Field f = ClassReflection.getDeclaredField(cache.getClass(), "methodLookup");
			f.setAccessible(true);
			return (ConsoleCache.MethodLookup)f.get(cache);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Test
	void addTest () {
		ConsoleCache.MethodLookup lookup = getMethodLookup(new ConsoleCache());

		executePrivateMethod(lookup, "addReferenceMethod", new Class[] {Method.class}, getMethod(TestClass.class, "hello"));
		executePrivateMethod(lookup, "addReferenceMethod", new Class[] {Method.class},
			getMethod(TestClass.class, "hello", int.class));
		executePrivateMethod(lookup, "addReferenceMethod", new Class[] {Method.class},
			getMethod(TestClass.class, "hello", String.class, int.class, double.class, char.class));

		assertNotNull(executePrivateMethod(lookup, "getReferenceMethod", new Class[] {Class.class, String.class, Class[].class},
			TestClass.class, "hello", null));
		assertNotNull(executePrivateMethod(lookup, "getReferenceMethod", new Class[] {Class.class, String.class, Class[].class},
			TestClass.class, "hello", new Class[] {int.class}));
		assertNotNull(executePrivateMethod(lookup, "getReferenceMethod", new Class[] {Class.class, String.class, Class[].class},
			TestClass.class, "hello", new Class[] {String.class, int.class, double.class, char.class}));
	}

	static class TestClass {
		public void hello () {
		}

		public void hello (int i) {
		}

		public void hello (String s, int i, double d, char c) {
		};
	}
}
