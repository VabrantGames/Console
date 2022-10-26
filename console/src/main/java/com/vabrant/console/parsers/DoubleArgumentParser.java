package com.vabrant.console.parsers;

import com.vabrant.console.ConsoleCache;

public class DoubleArgumentParser implements Parsable<ConsoleCacheAndStringData, Double> {
    @Override
    public Double parse(ConsoleCacheAndStringData data) throws RuntimeException {
        String text = data.getText();
        return Double.parseDouble(text.substring(0, text.length() - 1));
    }
}
