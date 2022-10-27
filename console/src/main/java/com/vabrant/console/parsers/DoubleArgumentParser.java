package com.vabrant.console.parsers;

public class DoubleArgumentParser implements Parsable<ConsoleCacheAndStringInput, Double> {
    @Override
    public Double parse(ConsoleCacheAndStringInput data) throws RuntimeException {
        String text = data.getText();
        return Double.parseDouble(text.substring(0, text.length() - 1));
    }
}
