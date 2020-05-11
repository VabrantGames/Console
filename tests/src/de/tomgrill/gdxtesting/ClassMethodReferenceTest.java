package de.tomgrill.gdxtesting;

import static org.junit.Assert.assertNotNull;

import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.Method;
import com.badlogic.gdx.utils.reflect.ReflectionException;
import com.vabrant.console.ClassMethodReference;
import com.vabrant.console.DebugLogger;

@RunWith(GdxTestRunner.class)
public class ClassMethodReferenceTest {

	@BeforeClass
	public static void init() {
		DebugLogger.usSysOut();
	}
	
	public Method getMethod(Class c, String name, Class... args) {
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
		ClassMethodReference.addReferenceMethod(getMethod(TestClass.class, "hello"));
		ClassMethodReference.addReferenceMethod(getMethod(TestClass.class, "hello", int.class));
		ClassMethodReference.addReferenceMethod(getMethod(TestClass.class, "hello", String.class, int.class, double.class, char.class));
		
		assertNotNull(ClassMethodReference.getReferenceMethod(TestClass.class, "hello"));
		assertNotNull(ClassMethodReference.getReferenceMethod(TestClass.class, "hello", int.class));
		assertNotNull(ClassMethodReference.getReferenceMethod(TestClass.class, "hello", String.class, int.class, double.class, char.class));
	}
	
	static class TestClass {
		public void hello() {}
		public void hello(int i) {}
		public void hello(String s, int i, double d, char c) {};
	}
}
