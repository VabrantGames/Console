
package com.vabrant.console.commandexecutor;

import com.badlogic.gdx.utils.ObjectMap;
import com.github.tommyettinger.ds.ObjectList;
import com.vabrant.console.commandexecutor.arguments.Argument;
import com.vabrant.console.commandexecutor.parsers.DefaultParserContext;
import com.vabrant.console.commandexecutor.parsers.Parsable;
import com.vabrant.console.commandexecutor.parsers.ParserContext;
import com.vabrant.console.DebugLogger;
import com.vabrant.console.log.LogLevel;
import com.vabrant.console.log.LogManager;

public abstract class AbstractCommandExecutor<T extends CommandCache> implements CommandExecutor<T> {

	protected CommandCache globalCache;
	protected DebugLogger logger;
	protected ObjectList<Argument> arguments;
	protected ObjectMap<Class, Parsable> parsers;
	protected ParserContext parserContext;
	protected LogManager logManager;
	protected CommandExecutorResult executionResult;

	protected AbstractCommandExecutor (LogManager logManager, CommandCache globalCache) {
		this.logManager = logManager;
		this.globalCache = globalCache;
		logger = new DebugLogger(this.getClass());
		arguments = new ObjectList<>();
		parsers = new ObjectMap<>();
		parserContext = new DefaultParserContext(globalCache);
		executionResult = new CommandExecutorResult();
	}

	public void setLogLevel (int level) {
		if (level == 0) return;

		if (logger == null) {
			logger = new DebugLogger(this.getClass(), level);
		} else {
			logger.setLevel(level);
		}
	}

	public DebugLogger getLogger () {
		if (logger == null) {
			logger = new DebugLogger(this.getClass());
		}

		return logger;
	}

	protected void printCommandToLogManager (String command) {
		if (logManager == null || command == null) return;
		logManager.add(null, "> " + command, LogLevel.INFO);
	}

	protected void printSuccessMessageToLogManager (String successMessage) {
		if (logManager == null || successMessage == null) return;
		logManager.add(null, successMessage, LogLevel.INFO);
	}

}
