package com.vabrant.console.executionstrategy;

import com.badlogic.gdx.utils.ObjectMap;
import com.vabrant.console.ConsoleCache;
import com.vabrant.console.ConsoleUtils;
import com.vabrant.console.MethodInfo;
import com.vabrant.console.arguments.*;
import com.vabrant.console.arguments.strategies.simple.SimpleDoubleArgumentStrategy;
import com.vabrant.console.arguments.strategies.simple.SimpleFloatArgumentStrategy;
import com.vabrant.console.arguments.strategies.simple.SimpleIntArgumentStrategy;
import com.vabrant.console.arguments.strategies.simple.SimpleLongArgumentStrategy;
import com.vabrant.console.parsers.*;
import com.vabrant.console.parsers.MethodArgumentInfoParser.MethodArgumentInfoParserInput;

public class SimpleExecutionStrategy implements ExecutionStrategy {

    private MethodArgumentInfoParserInput methodInfoParserInput;
    private ConsoleCacheAndStringInput cacheAndStringInput;
    private final ObjectMap<Class<?>, Argument> arguments;
    private ObjectMap<Class<?>, Parsable> parsers;

    public SimpleExecutionStrategy() {
        cacheAndStringInput = new ConsoleCacheAndStringInput();
        methodInfoParserInput = new MethodArgumentInfoParserInput();

        arguments = new ObjectMap<>(4);
        arguments.put(IntArgument.class, new IntArgument(new SimpleIntArgumentStrategy()));
        arguments.put(DoubleArgument.class, new DoubleArgument(new SimpleDoubleArgumentStrategy()));
        arguments.put(FloatArgument.class, new FloatArgument(new SimpleFloatArgumentStrategy()));
        arguments.put(LongArgument.class, new LongArgument(new SimpleLongArgumentStrategy()));

        parsers = new ObjectMap<>(5);
        parsers.put(MethodArgument.class, new MethodArgumentParser());
        parsers.put(DoubleArgument.class, new DoubleArgumentParser());
        parsers.put(IntArgument.class, new IntArgumentParser());
        parsers.put(FloatArgument.class, new FloatArgumentParser());
        parsers.put(LongArgument.class, new LongArgumentParser());
        parsers.put(MethodArgumentInfoParser.class, new MethodArgumentInfoParser());
    }

    @Override
    public Object execute(ExecutionStrategyInput input) throws Exception {
        ConsoleCache cache = input.getConsoleCache();
        String command = input.getText();

        cacheAndStringInput.setConsoleCache(cache);

        String[] sections = command.split(" ");
        MethodArgumentInfo info = (MethodArgumentInfo) parsers.get(MethodArgument.class).parse(cacheAndStringInput.setText(sections[0]));
        Object[] args = null;

        if (sections.length > 1) {
            String[] argsAsStr = new String[sections.length - 1];
            for (int i = 0; i < argsAsStr.length; i++) {
                argsAsStr[i] = sections[i + 1];
            }
            args = parseArgs(argsAsStr);
        } else {
            args = ConsoleUtils.EMPTY_ARGUMENTS;
        }

        methodInfoParserInput.setData(info);
        methodInfoParserInput.setArgs(args);

        ((MethodInfo) parsers.get(MethodArgumentInfoParser.class).parse(methodInfoParserInput)).invoke(args);

        return null;
    }

    private Object[] parseArgs(String[] argStrs) throws Exception {
        if (argStrs == null) return ConsoleUtils.EMPTY_ARGUMENTS;

        Object[] args = new Object[argStrs.length];

        outer:
        for (int i = 0; i < argStrs.length; i++) {
            String s = argStrs[i];

            for (Argument a : arguments.values()) {
                if (a.isType(s)) {
                    Parsable parser = parsers.get(a.getClass());
                    args[i] = parser.parse(cacheAndStringInput.setText(s));
                    continue outer;
                }
            }

            throw new RuntimeException("No Argument");
        }
        return args;
    }

}
