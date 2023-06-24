package com.vabrant.console;

public class CommandExecutionSettings {

    private boolean clearCommandOnFail = true;
    private boolean debugExecutionStrategy;

    public CommandExecutionSettings setClearCommandOnFail(boolean clearCommandOnFail) {
        this.clearCommandOnFail = clearCommandOnFail;
        return this;
    }

    public boolean clearCommandOnFail() {
        return clearCommandOnFail;
    }

    public CommandExecutionSettings setDebugExecutionStrategy(boolean debugExecutionStrategy) {
        this.debugExecutionStrategy = debugExecutionStrategy;
        return this;
    }

    public boolean debugExecutionStrategy() {
        return debugExecutionStrategy;
    }
}
