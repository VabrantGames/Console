package com.vabrant.console.parsers;

import com.vabrant.console.ConsoleCache;

public class IntArgumentParser implements Parsable<ConsoleCacheAndStringData, Integer> {

    @Override
    public Integer parse(ConsoleCacheAndStringData data) throws RuntimeException {
        int value = 0;
        String text = data.getText();

        if(text.startsWith("0b")) {
            value = Integer.parseInt(text.substring(2), 2);
        }
        else if(text.startsWith("0x")) {
            value = Integer.parseInt(text.substring(2), 16);
        }
        else if(text.startsWith("#")) {
            value = Integer.parseInt(text.substring(1), 16);
        }
        else {
            value = Integer.parseInt(text);
        }
        return value;
    }
}
