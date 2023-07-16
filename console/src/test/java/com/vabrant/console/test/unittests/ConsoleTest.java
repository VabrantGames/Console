package com.vabrant.console.test.unittests;

import com.vabrant.console.Console;
import com.vabrant.console.ConsoleStrategy;
import org.junit.jupiter.api.Test;

public class ConsoleTest {

	@Test
	void StrategyTest() {
		Console console = new Console();
		console.addStrategy("test", new TestStrategy());

		console.execute("/test hello fjwofjwo");
	}

	static class TestStrategy extends ConsoleStrategy {

		@Override
		public Object execute (Object o) throws Exception {
			System.out.println("print: " + (String)o);
			return null;
		}
	}
}
