package com.vabrant.console.parsers;

public class StringArgumentParser implements Parsable<ConsoleCacheAndStringInput, String> {

    @Override
    public String parse(ConsoleCacheAndStringInput data) throws RuntimeException {
        return data.getText();
    }
}
