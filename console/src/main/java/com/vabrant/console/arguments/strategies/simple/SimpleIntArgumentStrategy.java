package com.vabrant.console.arguments.strategies.simple;

import com.vabrant.console.arguments.Argument;

public class SimpleIntArgumentStrategy implements Argument.ArgumentStrategy<String> {

    @Override
    public boolean isType(String s) {
        return Character.isDigit(s.charAt(0)) && !s.contains(".") && Character.isDigit(s.charAt(s.length() - 1));
    }
}
