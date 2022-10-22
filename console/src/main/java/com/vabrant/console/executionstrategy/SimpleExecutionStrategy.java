package com.vabrant.console.executionstrategy;

import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectSet;
import com.vabrant.console.*;
import com.vabrant.console.commandsections.Argument;
import com.vabrant.console.commandsections.IntArgument;
import com.vabrant.console.commandsections.MethodArgument;
import com.vabrant.console.commandsections.Parsable;

public class SimpleExecutionStrategy implements ExecutionStrategy {

    private final ObjectMap<Class<?>, Argument> arguments;

    public SimpleExecutionStrategy() {
        arguments = new ObjectMap<>(10);
        arguments.put(MethodArgument.class,  new MethodArgument());
        arguments.put(IntArgument.class, new IntArgument());
    }


    @Override
    public void execute(ConsoleCache cache, String command) {
        String[] sections = command.split(" ");
        String[] strArgs = null;

        Parsable<MethodArgument.MethodArgumentInfo> arg = (Parsable) arguments.get(MethodArgument.class);

        MethodArgument.MethodArgumentInfo info = arg.parse(cache, sections[0]);
        strArgs = createArgumentArray(1, sections);

        MethodReference methodRef = null;
        Object[] args = parseArgs(strArgs);

        if (info.getClassReference() == null) {
            for (MethodInfo mi : info.getMethods()) {
                if (ConsoleUtils.areArgsEqual(mi.getMethodReference().getArgs(), toArgClassArray(args))) {
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

    private Class<?>[] toArgClassArray(Object[] args) {
        Class<?>[] cls = new Class<?>[args.length];
        for (int i = 0; i < args.length; i++) {
            cls[i] = args[i].getClass();
        }
        return cls;
    }

    private Object[] parseArgs(String[] argStrs) {
        if (argStrs == null) return ConsoleUtils.EMPTY_ARGUMENTS;

        Object[] args = new Object[argStrs.length];

        for (int i = 0; i < argStrs.length; i++) {
            String s = argStrs[i];

            if (Character.isDigit(s.charAt(0))) {
                char lastChar = s.charAt(s.length() - 1);

                if (lastChar == 'f' || lastChar == 'F') {
                    args[i] = Float.parseFloat(s.substring(0, s.length() - 1));
                } else if (lastChar == 'd' || lastChar == 'D') {
                    args[i] = Double.parseDouble(s.substring(0, s.length() - 1));
                } else if (lastChar == 'l' || lastChar == 'L') {
                    args[i] = Long.parseLong(s.substring(0, s.length() - 1));
                } else {
                    args[i] = Integer.parseInt(s);
                }
            }
        }
        return args;
    }

    private String[] createArgumentArray(int start, String[] sections) {
        if (start >= sections.length) return null;

        String[] args = new String[sections.length - start];
        for (int i = 0; i < args.length; i++) {
            args[i] = sections[start + i];
        }
        return args;
    }
}
