package com.vabrant.console.parsers;

public class FloatArgumentParser implements Parsable<ConsoleCacheAndStringData, Float> {

    @Override
    public Float parse(ConsoleCacheAndStringData data) throws RuntimeException {
        return Float.parseFloat(data.getText());
    }
}
