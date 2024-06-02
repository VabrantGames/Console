
package com.vabrant.console.CommandEngine;

public interface CommandEngine<T extends CommandCache> {
	CommandEngineExecutionResult execute (T cache, Object o) throws Exception;
}
