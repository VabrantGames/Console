
package com.vabrant.console.commandexecutor;

public interface CommandExecutor<T extends CommandCache> {
	CommandExecutorResult execute (T cache, Object o) throws Exception;
}
