package com.vabrant.console.parsers;

public class StringArgumentParser implements Parsable<ConsoleCacheAndStringInput, String> {

    @Override
    public String parse(ConsoleCacheAndStringInput data) throws RuntimeException {
        String str = data.getText();
        if (str.charAt(0) == '"') {
            return str.substring(1, str.length() - 1);
        } else {
            return str;
        }
    }
}
