
package com.vabrant.console;

import com.badlogic.gdx.utils.StringBuilder;
import com.vabrant.console.log.Log;
import com.vabrant.console.log.LogLevel;
import com.vabrant.console.log.LogManager;

public class Console {

	private boolean logToSystem;
	private boolean printStackTraceToSystemOnError;
	protected final LogManager logManager;
	private StringBuilder stringBuilder;
	public CommandExecutionStrategy commandExecutionStrategy;
	public CommandExecutionData commandExecutionData;

	public Console () {
		commandExecutionData = new CommandExecutionData();
		commandExecutionStrategy = new CommandExecutionStrategy();
		commandExecutionStrategy.setConsole(this);
		commandExecutionStrategy.setData(commandExecutionData);
		logManager = new LogManager();
	}

	public CommandExecutionData getCommandExecutionData() {
		return commandExecutionData;
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
		commandExecutionData.setConsoleCache(cache);
	}

	public ConsoleCache getCache () {
		return commandExecutionData.getConsoleCache();
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
