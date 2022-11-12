package com.vabrant.console;

import com.badlogic.gdx.utils.StringBuilder;
import com.vabrant.console.executionstrategy.ExecutionStrategy;
import com.vabrant.console.executionstrategy.ExecutionStrategyInput;
import com.vabrant.console.log.Log;
import com.vabrant.console.log.LogLevel;
import com.vabrant.console.log.LogManager;

public class Console implements Executable<String, Boolean> {

    private boolean logToSystem;
    public final DebugLogger logger = new DebugLogger(Console.class, DebugLogger.DEBUG);
    private ConsoleCache cache;
    private ExecutionStrategy executionStrategy;
    private final ExecutionStrategyInput executionStrategyInput;
    private final LogManager logManager;
    private StringBuilder stringBuilder;

    public Console(ExecutionStrategy executionStrategy) {
        this.executionStrategy = executionStrategy;
        executionStrategyInput = new ExecutionStrategyInput();
        logManager = new LogManager();
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

    public void logToSystem(boolean logToSystem) {
        this.logToSystem = logToSystem;
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

    public void log(String message) {
        log(null, message, LogLevel.INFO);
    }

    public void log(String message, LogLevel level) {
        log(null, message, level);
    }

    public void log(String tag, String message, LogLevel level) {
        Log log = logManager.add(tag, message, level);
        if (logToSystem) logToSystem(log);
    }

    private void logToSystem(Log log) {
        if (stringBuilder == null) stringBuilder = new StringBuilder(100);

        stringBuilder.clear();

        stringBuilder.append('(');
        stringBuilder.append(log.getLogLevel().name());
        stringBuilder.append(") ");

        if (log.getTag() != null) {
            stringBuilder.append('[');
            stringBuilder.append(log.getTag());
            stringBuilder.append("] : ");
        }

        stringBuilder.append(log.getMessage());

        if (!log.getLogLevel().equals(LogLevel.ERROR)) {
            System.out.println(stringBuilder.toString());
        } else {
            System.err.println(stringBuilder.toString());
        }
    }

}
