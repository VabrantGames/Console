
package com.vabrant.console.CommandEngine;

import com.badlogic.gdx.utils.ObjectMap;
import com.github.tommyettinger.ds.ObjectList;
import com.vabrant.console.CommandEngine.arguments.Argument;
import com.vabrant.console.CommandEngine.parsers.DefaultParserContext;
import com.vabrant.console.CommandEngine.parsers.Parsable;
import com.vabrant.console.CommandEngine.parsers.ParserContext;
import com.vabrant.console.DebugLogger;
import com.vabrant.console.log.LogLevel;
import com.vabrant.console.log.LogManager;

public abstract class AbstractCommandEngine<T extends CommandCache> implements CommandEngine<T> {

	protected CommandCache globalCache;
	protected DebugLogger logger;
	protected ObjectList<Argument> arguments;
	protected ObjectMap<Class, Parsable> parsers;
	protected ParserContext parserContext;
// protected Console console;
	protected LogManager logManager;
	protected CommandEngineExecutionResult executionResult;

	protected AbstractCommandEngine (LogManager logManager, CommandCache globalCache) {
		this.logManager = logManager;
		this.globalCache = globalCache;
		logger = new DebugLogger(this.getClass());
		arguments = new ObjectList<>();
		parsers = new ObjectMap<>();
		parserContext = new DefaultParserContext(globalCache);
		executionResult = new CommandEngineExecutionResult();
	}

	protected void printCommandToLogManager (String command) {
		if (logManager == null || command == null) return;
		logManager.add(null, "> " + command, LogLevel.INFO);
	}

	protected void printSuccessMessageToLogManager (String successMessage) {
		if (logManager == null || successMessage == null) return;
		logManager.add(null, successMessage, LogLevel.INFO);
	}

// @Override
// public CommandEngineExecutionResult execute (CommandCache cache, Object o) throws Exception {
// return null;
// }
}
