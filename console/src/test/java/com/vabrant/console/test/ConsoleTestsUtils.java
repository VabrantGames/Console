
package com.vabrant.console.test;

import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.Method;
import com.vabrant.console.commandexecutor.Command;
import com.vabrant.console.commandexecutor.ConsoleCommand;
import com.vabrant.console.commandexecutor.ConsoleReference;

public class ConsoleTestsUtils {

	public static Object executePrivateMethod (Object obj, String methodName, Class[] parameters, Object... args) {
		try {
			Method m = ClassReflection.getDeclaredMethod(obj.getClass(), methodName, parameters);
			m.setAccessible(true);
			return m.invoke(obj, args);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static class CustomCommand implements Command {

		@Override
		public void setSuccessMessage (String successMessage) {

		}

		@Override
		public String getSuccessMessage () {
			return null;
		}

		@Override
		public Object execute (Object[] args) throws Exception {
			return 1;
		}

		@Override
		public Class getReturnType () {
			return int.class;
		}
	}

	@ConsoleReference("helloClass")
	public static class TestClass {

		@ConsoleReference("helloWorld") public String helloWorld = "Hello World";

		@ConsoleCommand(successMessage = "Hello")
		public void hello () {
		}

		@ConsoleCommand
		public void hello (String s) {
		}

		@ConsoleCommand
		public void hello (int i) {
		}

		@ConsoleCommand
		public int add (int x1, int x2) {
			return x1 + x2;
		}

		@ConsoleCommand
		public static void helloAll () {
		}
	}
}
