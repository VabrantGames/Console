package com.vabrant.console.parsers;

import com.vabrant.console.ConsoleCache;

public class StringArgumentParser implements Parsable<ConsoleCacheAndStringData, String> {

    @Override
    public String parse(ConsoleCacheAndStringData data) throws RuntimeException {
        return data.getText();
    }
}
