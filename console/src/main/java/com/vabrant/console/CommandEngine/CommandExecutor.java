
package com.vabrant.console.CommandEngine;

public interface CommandExecutor<T extends CommandCache> {
	CommandExecutorExecutionResult execute (T cache, Object o) throws Exception;
}
