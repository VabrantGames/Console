package com.vabrant.console;

public class NoMethodFoundException extends RuntimeException {

    public NoMethodFoundException(String methodName) {
        super("Method " + methodName + " not found");
    }
}
