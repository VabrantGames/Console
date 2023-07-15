
package com.vabrant.console;

import com.badlogic.gdx.utils.StringBuilder;
import com.vabrant.console.log.LogLevel;

public class Console implements Executable<Object, Boolean> {

	private boolean logToSystem = true;
	private boolean printStackTraceToSystemOnError;
	private StringBuilder stringBuilder;

	public Console () {
		this(null);
	}

	public Console (ConsoleConfiguration settings) {
		if (settings == null) return;
		logToSystem(settings.logToSystem);
	}

	@Override
	public Boolean execute (Object o) throws Exception {
		return false;
	}

	public void logToSystem (boolean logToSystem) {
		this.logToSystem = logToSystem;
	}

	public void printStackTrackToSystemOnError (boolean printToSystem) {
		printStackTraceToSystemOnError = printToSystem;
	}

	public void log (String message) {
		log(null, message, LogLevel.INFO);
	}

	public void log (String message, LogLevel level) {
		log(null, message, level);
	}

	public void log (String tag, String message, LogLevel level) {
		if (logToSystem) logToSystem(tag, message, level);
	}

	protected void logToSystem (String tag, String message, LogLevel level) {
		if (stringBuilder == null) stringBuilder = new StringBuilder(100);

		stringBuilder.clear();

		stringBuilder.append('(');
		stringBuilder.append(level.name());
		stringBuilder.append(") ");

		if (tag != null) {
			stringBuilder.append('[');
			stringBuilder.append(tag);
			stringBuilder.append("] : ");
		}

		stringBuilder.append(message);

		if (!level.equals(LogLevel.ERROR)) {
			System.out.println(stringBuilder.toString());
		} else {
			System.err.println(stringBuilder.toString());
		}
	}

}
