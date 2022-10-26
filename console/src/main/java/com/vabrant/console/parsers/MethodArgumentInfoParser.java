package com.vabrant.console.parsers;

import com.vabrant.console.MethodInfo;

public class MethodArgumentInfoParser implements Parsable<MethodArgumentInfoParser.MethodArgumentInfoParserData, MethodInfo> {

    @Override
    public MethodInfo parse(MethodArgumentInfoParserData methodArgumentInfoParserData) throws RuntimeException {
        return null;
    }

    public static class MethodArgumentInfoParserData {

        private MethodArgumentInfo info;
        private String[] argsAsStr;

    }
}
