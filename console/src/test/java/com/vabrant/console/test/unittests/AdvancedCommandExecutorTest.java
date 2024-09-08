
package com.vabrant.console.test.unittests;

import com.vabrant.console.commandexecutor.*;
import com.vabrant.console.commandexecutor.AdvancedCommandExecutor;
import com.vabrant.console.DebugLogger;
import com.vabrant.console.test.ConsoleTestsUtils.TestClass;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AdvancedCommandExecutorTest {

	private static AdvancedCommandExecutor executor;
	private static DefaultCommandCache globalCache;

	@BeforeAll
	public static void init () {
		DebugLogger.useSysOut();
		globalCache = new DefaultCommandCache();
		executor = new AdvancedCommandExecutor(null, globalCache);
		executor.setLogLevel(DebugLogger.DEBUG);
// executor.printStackTrace(true);
	}

	@Test
	void StringTest () throws Exception {
		CommandExecutorResult result = null;
		DefaultCommandCache cache = new DefaultCommandCache();
		cache.addAll(new TestClass());
		cache.addCommand("getNumber", new GetNumberCommand());

		// Test basic and more advanced commands
		assertTrue(executor.execute(cache, "hello").getExecutionStatus());
		assertTrue((result = executor.execute(cache, "add (10 ,10)")).getExecutionStatus(), result.getErrorString());
		assertTrue(executor.execute(cache, "add (10 , add(10, 10))").getExecutionStatus());

		// Ensure that custom commands execute correctly
		result = executor.execute(cache, "add (10, .getNumber)");
		assertTrue(result.getExecutionStatus());
		assertEquals(110, (int)result.getResult());
	}

	private static class GetNumberCommand implements Command {
		@Override
		public void setSuccessMessage (String successMessage) {
		}

		@Override
		public String getSuccessMessage () {
			return null;
		}

		@Override
		public Object execute (Object[] args) throws Exception {
			return 100;
		}

		@Override
		public Class getReturnType () {
			return int.class;
		}
	}

// @ConsoleReference("test")
// private static class TestClass {
//
// @ConsoleCommand
// public void hello () {
// System.out.println("Hello");
// }
//
// @ConsoleCommand
// public int add (int x1, int x2) {
// return x1 + x2;
// }
//
// }
}
