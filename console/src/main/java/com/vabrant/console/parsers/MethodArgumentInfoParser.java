package com.vabrant.console.parsers;

import com.vabrant.console.ConsoleUtils;
import com.vabrant.console.MethodInfo;
import com.badlogic.gdx.utils.StringBuilder;

public class MethodArgumentInfoParser implements Parsable<MethodArgumentInfoParser.MethodArgumentInfoParserInput, MethodInfo> {

    private final StringBuilder builder;

    public MethodArgumentInfoParser() {
        builder = new StringBuilder(50);
    }

    @Override
    public MethodInfo parse(MethodArgumentInfoParserInput input) throws RuntimeException {
        MethodArgumentInfo data = input.getData();

        Object[] args = input.getArgs();
        Class[] types = null;

        if (args.length == 0) {
           types = ConsoleUtils.EMPTY_ARGUMENT_TYPES;
        } else {
            types = new Class[args.length];
            for (int i = 0; i < types.length; i++) {
                types[i] = args[i].getClass();
            }
        }

        for (MethodInfo mi : data.getMethods()) {
            if (ConsoleUtils.areArgsEqual(mi.getMethodReference().getArgs(), types)) {
                return mi;
            }
        }

        throw new RuntimeException("[NoMethodFound] : [Name]:" + data.getMethodName() + " [Reference]:" + data.getReferenceName() + " [Args]:" + userArgsToString(args));
    }

    private String userArgsToString(Object[] args) {
        builder.clear();

        builder.append('{');
        for (int i = 0; i < args.length; i++) {
            builder.append(args[i].getClass().getSimpleName());
            if (i < (args.length - 1)) builder.append(", ");
        }
        builder.append('}');

        return builder.toString();
    }

    public static class MethodArgumentInfoParserInput {

        private MethodArgumentInfo info;
        private Object[] args;

        public void setData(MethodArgumentInfo info) {
            this.info = info;
        }

        public void setArgs(Object[] args) {
            this.args = args;
        }

        public MethodArgumentInfo getData() {
            return info;
        }

        public Object[] getArgs() {
            return args;
        }

    }
}
