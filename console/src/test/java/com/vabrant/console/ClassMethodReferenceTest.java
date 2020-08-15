package com.vabrant.console;


import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.Method;
import com.badlogic.gdx.utils.reflect.ReflectionException;

public class ClassMethodReferenceTest {

	@BeforeAll
	public static void init() {
		DebugLogger.useSysOut();
	}
	
	private Method getMethod(Class<?> c, String name, Class<?>... args) {
		try {
			Method m = ClassReflection.getMethod(c, name, args);
			m.setAccessible(true);
			return m;
		}
		catch(ReflectionException e) {
			e.printStackTrace();
			Gdx.app.exit();
		}
		return null;
	}
	
	@Test
	public void addTest() {
		ClassMethodReference classMethodReference = new ClassMethodReference();
		
		classMethodReference.addReferenceMethod(getMethod(TestClass.class, "hello"));
		classMethodReference.addReferenceMethod(getMethod(TestClass.class, "hello", int.class));
		classMethodReference.addReferenceMethod(getMethod(TestClass.class, "hello", String.class, int.class, double.class, char.class));
		
		assertNotNull(classMethodReference.getReferenceMethod(TestClass.class, "hello"));
		assertNotNull(classMethodReference.getReferenceMethod(TestClass.class, "hello", int.class));
		assertNotNull(classMethodReference.getReferenceMethod(TestClass.class, "hello", String.class, int.class, double.class, char.class));
	}
	
	static class TestClass {
		public void hello() {}
		public void hello(int i) {}
		public void hello(String s, int i, double d, char c) {};
	}
}
