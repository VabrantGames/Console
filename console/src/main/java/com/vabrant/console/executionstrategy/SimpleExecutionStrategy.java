package com.vabrant.console.executionstrategy;

import com.badlogic.gdx.utils.ObjectMap;
import com.vabrant.console.ConsoleCache;
import com.vabrant.console.ConsoleUtils;
import com.vabrant.console.MethodInfo;
import com.vabrant.console.MethodReference;
import com.vabrant.console.arguments.*;
import com.vabrant.console.arguments.strategies.simple.SimpleDoubleArgumentStrategy;
import com.vabrant.console.arguments.strategies.simple.SimpleFloatArgumentStrategy;
import com.vabrant.console.arguments.strategies.simple.SimpleIntArgumentStrategy;
import com.vabrant.console.arguments.strategies.simple.SimpleLongArgumentStrategy;
import com.vabrant.console.parsers.*;

public class SimpleExecutionStrategy implements ExecutionStrategy {

    private ConsoleCacheAndStringData cacheAndStringData;
    private final ObjectMap<Class<?>, Argument> arguments;
    private ObjectMap<Class<?>, Parsable> parsers;

    public SimpleExecutionStrategy() {
        cacheAndStringData = new ConsoleCacheAndStringData();

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
    }

    @Override
    public void execute(ConsoleCache cache, String command) {
        cacheAndStringData.setConsoleCache(cache);

        String[] sections = command.split(" ");
//        String[] strArgs = createArgumentArray(1, sections);
        MethodArgumentInfo info = (MethodArgumentInfo) parsers.get(MethodArgument.class).parse(cacheAndStringData.setText(sections[0]));
        String[] argsAsStr = new String[sections.length - 1];

        if (argsAsStr.length > 0) {
             for (int i = 0; i < argsAsStr.length; i++) {
                 argsAsStr[i] = sections[i + 1];
             }
        }

        MethodReference methodRef = null;
        Object[] args = parseArgs(argsAsStr);

        if (info.getClassReference() == null) {
            for (MethodInfo mi : info.getMethods()) {
                if (ConsoleUtils.areArgsEqual(mi.getMethodReference().getArgs(), argsAsClass(args))) {
//                    methodRef = cache.getClassMethodReference().getReferenceMethod(mi);
                    methodRef = mi.getMethodReference();
                    info.setClassReference(mi.getClassReference());
//                    classRef = mi.getClassReference();
                    break;
                }
            }
        } else {

        }

        if (methodRef == null) throw new RuntimeException("No method found");

        try {
            methodRef.invoke(info.getClassReference().getReference(), args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Class<?>[] argsAsClass(Object[] args) {
        Class<?>[] cls = new Class<?>[args.length];
        for (int i = 0; i < args.length; i++) {
            cls[i] = args[i].getClass();
        }
        return cls;
    }

    private Object[] parseArgs(String[] argStrs) {
        if (argStrs == null) return ConsoleUtils.EMPTY_ARGUMENTS;

        Object[] args = new Object[argStrs.length];

        outer:
        for (int i = 0; i < argStrs.length; i++) {
            String s = argStrs[i];

            for (Argument a : arguments.values()) {
                if (a.isType(s)) {
                    Parsable parser = parsers.get(a.getClass());
                    args[i] = parser.parse(cacheAndStringData.setText(s));
                    continue outer;
                }
            }

            throw new RuntimeException("No Argument");
        }
        return args;
    }

}
