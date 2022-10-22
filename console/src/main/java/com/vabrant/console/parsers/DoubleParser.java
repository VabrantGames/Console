package com.vabrant.console.parsers;

import com.vabrant.console.ConsoleCache;
import com.vabrant.console.commandsections.Parsable;

public class DoubleParser implements Parsable<Double> {
    @Override
    public Double parse(ConsoleCache cache, String text) throws RuntimeException {
        return Double.parseDouble(text);
    }
}
