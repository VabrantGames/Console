
package com.vabrant.console;

import com.badlogic.gdx.utils.StringBuilder;
import com.vabrant.console.executionstrategy.CommandExecutionStrategy;
import com.vabrant.console.log.Log;
import com.vabrant.console.log.LogLevel;
import com.vabrant.console.log.LogManager;

public class Console {

	private boolean logToSystem;
	private boolean printStackTraceToSystemOnError;
	private CommandExecutionStrategy commandExecutionStrategy;
	private final LogManager logManager;
	private StringBuilder stringBuilder;

	public Console () {
		this(null);
	}

	public Console (ExecutionStrategy executionStrategy) {
		commandExecutionStrategy = new CommandExecutionStrategy();
// this.executionStrategy = executionStrategy;
// commandExecutionStrategyContext = new CommandExecutionStrategyContext();
		commandExecutionStrategy.setConsole(this);
		logManager = new LogManager();
	}

	public CommandExecutionStrategy getCommandExecutionStrategy () {
		return commandExecutionStrategy;
	}

	public boolean executeCommand (String s) {
		return commandExecutionStrategy.execute(s);
	}

	public void logToSystem (boolean logToSystem) {
		this.logToSystem = logToSystem;
	}

	public void printStackTrackToSystemOnError (boolean printToSystem) {
		printStackTraceToSystemOnError = printToSystem;
	}

	public void setCache (ConsoleCache cache) {
		commandExecutionStrategy.setConsoleCache(cache);
	}

// public void setStrategy (ExecutionStrategy strategy) {
// executionStrategy = strategy;
// strategy}

	public ConsoleCache getCache () {
		return commandExecutionStrategy.getConsoleCache();
	}

	public void log (String message) {
		log(null, message, LogLevel.INFO);
	}

	public void log (String message, LogLevel level) {
		log(null, message, level);
	}

	public void log (String tag, String message, LogLevel level) {
		Log log = logManager.add(tag, message, level);
		if (logToSystem) logToSystem(log);
	}

	private void logToSystem (Log log) {
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
