package com.vabrant.console;

public class CommandExecutionSettings {

    private boolean clearCommandOnFail = true;
    private boolean debugExecutionStrategy;
    private boolean useCustomTextFieldInput;

    public CommandExecutionSettings useCustomTextFieldInput(boolean customInput) {
        useCustomTextFieldInput = customInput;
        return this;
    }

    public boolean useCustomTextFieldInput() {
        return useCustomTextFieldInput;
    }

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
