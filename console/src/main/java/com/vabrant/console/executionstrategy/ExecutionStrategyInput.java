
package com.vabrant.console.executionstrategy;

import com.vabrant.console.ConsoleCache;

public class ExecutionStrategyInput {

	private String text;
	private ConsoleCache cache;

	public ExecutionStrategyInput setConsoleCache (ConsoleCache cache) {
		this.cache = cache;
		return this;
	}

	public ConsoleCache getConsoleCache () {
		return cache;
	}

	public ExecutionStrategyInput setText (String text) {
		this.text = text;
		return this;
	}

	public String getText () {
		return text;
	}
}
