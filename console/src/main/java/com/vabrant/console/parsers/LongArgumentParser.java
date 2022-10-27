package com.vabrant.console.parsers;

public class LongArgumentParser implements Parsable<ConsoleCacheAndStringInput, Long> {

    @Override
    public Long parse(ConsoleCacheAndStringInput data) throws RuntimeException {
        String text = data.getText();
        return Long.parseLong(text.substring(0, text.length() - 1));
    }
}
