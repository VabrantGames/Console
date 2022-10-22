package com.vabrant.console.arguments;

public class MethodArgument extends Argument {

    public MethodArgument() {
        super(null);
    }

    static class SimpleArgumentLogic implements Argument.ArgumentLogic {

        @Override
        public boolean isType() {
            return false;
        }
    }
}
