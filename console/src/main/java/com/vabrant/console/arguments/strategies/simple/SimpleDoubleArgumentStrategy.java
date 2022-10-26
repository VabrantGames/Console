package com.vabrant.console.arguments.strategies.simple;

import com.vabrant.console.arguments.Argument;

public class SimpleDoubleArgumentStrategy implements Argument.ArgumentStrategy<String> {

    @Override
    public boolean isType(String s) {
        if (Character.isDigit(s.charAt(0)) || s.charAt(0) == '.') {
            char lastChar = s.charAt(s.length() - 1);
            return lastChar == 'd' || lastChar == 'D';
        }
        return false;
    }
}