package com.vabrant.console.parsers;

import com.vabrant.console.ConsoleCache;

public class LongArgumentParser implements Parsable<ConsoleCacheAndStringData, Long> {

    @Override
    public Long parse(ConsoleCacheAndStringData data) throws RuntimeException {
        String text = data.getText();
        return Long.parseLong(text.substring(0, text.length() - 1));
    }
}
