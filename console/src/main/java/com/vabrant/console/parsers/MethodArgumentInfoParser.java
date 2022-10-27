package com.vabrant.console.parsers;

import com.vabrant.console.ConsoleUtils;
import com.vabrant.console.MethodInfo;

public class MethodArgumentInfoParser implements Parsable<MethodArgumentInfoParser.MethodArgumentInfoParserInput, MethodInfo> {

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

        if (data.getClassReference() == null) {
            for (MethodInfo mi : data.getMethods()) {
                if (ConsoleUtils.areArgsEqual(mi.getMethodReference().getArgs(), types)) {
                    return mi;
                }
            }
        } else {

        }

        throw new RuntimeException("No method found");
    }

    private Class[] argsAsClass(Object[] args) {
        if (args.length == 0) return ConsoleUtils.EMPTY_ARGUMENT_TYPES;

        Class[] argsAsClass = new Class[args.length];


        return null;
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
