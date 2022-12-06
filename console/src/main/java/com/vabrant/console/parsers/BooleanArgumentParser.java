package com.vabrant.console.parsers;

public class BooleanArgumentParser implements Parsable<ConsoleCacheAndStringInput, Boolean> {

    @Override
    public Boolean parse(ConsoleCacheAndStringInput data) throws Exception {
        return Boolean.parseBoolean(data.getText());
    }

}
