package com.vabrant.console;

import com.vabrant.console.executionstrategy.ExecutionStrategy;
import com.vabrant.console.executionstrategy.ExecutionStrategyInput;

public class Console implements Executable<String, Boolean> {

    public final DebugLogger logger = new DebugLogger(Console.class, DebugLogger.DEBUG);
    private ConsoleCache cache;
    private ExecutionStrategy executionStrategy;
    private final ExecutionStrategyInput executionStrategyInput;

    public Console(ExecutionStrategy executionStrategy) {
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
        if (cache == null) throw new IllegalArgumentException("Cache is null.");
        this.cache = cache;
        executionStrategyInput.setConsoleCache(cache);
    }

    public void setStrategy(ExecutionStrategy strategy) {
        executionStrategy = strategy;
    }

    public ConsoleCache getCache() {
        return cache;
    }

}
