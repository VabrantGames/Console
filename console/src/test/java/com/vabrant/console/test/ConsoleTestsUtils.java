package com.vabrant.console.test;

import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.Method;

public class ConsoleTestsUtils {

    public static Object executePrivateMethod(Object obj, String methodName, Class[] parameters, Object... args) {
        try {
            Method m = ClassReflection.getDeclaredMethod(obj.getClass(), methodName, parameters);
            m.setAccessible(true);
            return m.invoke(obj, args);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
