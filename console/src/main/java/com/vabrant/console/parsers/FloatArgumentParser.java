package com.vabrant.console.parsers;

public class FloatArgumentParser implements Parsable<ConsoleCacheAndStringInput, Float> {

    @Override
    public Float parse(ConsoleCacheAndStringInput data) throws RuntimeException {
        return Float.parseFloat(data.getText());
    }
}
