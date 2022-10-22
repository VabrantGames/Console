package com.vabrant.console.test.unittests;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.Method;
import com.badlogic.gdx.utils.reflect.ReflectionException;
import com.vabrant.console.ClassMethodReference;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

//import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ClassMethodReferenceTest {

	private static Application application;

	@BeforeAll
	public static void init() {
		application = new HeadlessApplication(new ApplicationAdapter() {});
//		DebugLogger.useSysOut();
	}

	private Object executePrivateMethod(Object obj, String methodName, Object... args) {
		Class<?>[] parameters = new Class[args.length];
		for (int i = 0; i < args.length; i++) {
			parameters[i] = args[i].getClass();
			System.out.println(args[i].getClass());
		}

		try {
			Method m = ClassReflection.getDeclaredMethod(obj.getClass(), methodName, parameters);
			m.setAccessible(true);
			m.invoke(obj, args);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
		return null;
	}
	
	private Method getMethod(Class<?> c, String name, Class<?>... args) {
		try {
			Method m = ClassReflection.getMethod(c, name, args);
			m.setAccessible(true);
			return m;
		}
		catch(Exception e) {
			e.printStackTrace();
			Gdx.app.exit();
		}
		return null;
	}
	
	@Test
	public void addTest() {
		ClassMethodReference classMethodReference = new ClassMethodReference();

		executePrivateMethod(classMethodReference, "addReferenceMethod", getMethod(TestClass.class, "hello"));
		executePrivateMethod(classMethodReference, "addReferenceMethod", getMethod(TestClass.class, "hello", int.class));
		executePrivateMethod(classMethodReference, "addReferenceMethod", getMethod(TestClass.class, "hello", String.class, int.class, double.class, char.class));
		
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
