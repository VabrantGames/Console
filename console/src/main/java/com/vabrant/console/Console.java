package com.vabrant.console;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.vabrant.console.commandsections.Executable;
import com.vabrant.console.executionstrategy.ExecutionStrategy;
import com.vabrant.console.executionstrategy.ExecutionStrategyInput;

public class Console implements Executable<String, Boolean> {

	public enum ExecutionStrategyType {
		SIMPLE
	}
	
	public final DebugLogger logger = new DebugLogger(Console.class, DebugLogger.DEBUG);
	private ConsoleCache cache;
//	private ConsoleCache globalCache;
	private ExecutionStrategy executionStrategy;
	private ExecutionStrategyInput executionStrategyInput;

	public Console(ExecutionStrategy executionStrategy) {
		//TODO REMOVE! FOR DEBUGGING ONLY!
		Gdx.app.setLogLevel(Application.LOG_DEBUG);

		this.executionStrategy = executionStrategy;
		executionStrategyInput = new ExecutionStrategyInput();
	}

	@Override
	public Boolean execute(String s) {
		try {
			if (cache == null) throw new RuntimeException("No cache set");
			executionStrategyInput.setText(s);
			executionStrategy.execute(executionStrategyInput);
		} catch (Exception e) {
			System.err.println(e.getMessage());
			return false;
		}
		return true;
	}

	public void setCache(ConsoleCache cache) {
		if(cache == null) throw new IllegalArgumentException("Cache is null.");
		this.cache = cache;
		executionStrategyInput.setConsoleCache(cache);
	}
	
	public ConsoleCache getCache() {
		return cache;
	}

}
