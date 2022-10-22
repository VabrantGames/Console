package com.vabrant.console.executionstrategy;

import com.vabrant.console.Console;
import com.vabrant.console.ConsoleCache;

public interface ExecutionStrategy {
    void execute(ConsoleCache cache, String command);
}
