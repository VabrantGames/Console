package com.vabrant.console;

public interface Command<T, U> {
    U execute(T t);
}
