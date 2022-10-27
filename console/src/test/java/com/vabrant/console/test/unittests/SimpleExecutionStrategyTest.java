package com.vabrant.console.test.unittests;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.vabrant.console.ConsoleCache;
import com.vabrant.console.annotation.ConsoleMethod;
import com.vabrant.console.annotation.ConsoleObject;
import com.vabrant.console.executionstrategy.ExecutionStrategyInput;
import com.vabrant.console.executionstrategy.SimpleExecutionStrategy;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class SimpleExecutionStrategyTest {

    private static Application application;

    @BeforeAll
    public static void init() {
        application = new HeadlessApplication(new ApplicationAdapter() {
        });
        Gdx.app.setLogLevel(Application.LOG_DEBUG);
    }

    @Test
    public void basic() {
        TestClass c = new TestClass();

        ConsoleCache cache = new ConsoleCache();
        cache.add(new TestClass(), "test");
//        cache.addMethod(c, "printAge", int.class);
//        cache.addMethod(c, "hello");

        ExecutionStrategyInput input = new ExecutionStrategyInput();
        SimpleExecutionStrategy strategy = new SimpleExecutionStrategy();

        input.setConsoleCache(cache);

        try {
//        strategy.execute(cache, "test.hello");
//        strategy.execute(cache, ".printAge 28");
            strategy.execute(input.setText(".hello"));
            strategy.execute(input.setText(".hello 89"));
//            strategy.execute(cache, ".printFloat 5d");
//        strategy.execute(cache, ".printStats 5 5f 10d 5L");
//        strategy.execute(cache, "printAge 28");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    @ConsoleObject
    static class TestClass {

        @ConsoleMethod
        public void hello() {
            System.out.println("Hello");
        }

        @ConsoleMethod
        public void printAge(int age) {
            System.out.println("My age is " + age);
        }

        @ConsoleMethod
        public void printLong(long l) {
            System.out.println("long: " + l);
        }

        @ConsoleMethod
        public void printFloat(float f) {
            System.out.println("float: " + f);
        }

        @ConsoleMethod
        public void printDouble(double d) {
            System.out.println("double: " + d);
        }

        @ConsoleMethod
        public void printStats(int i1, float f1, double d1, long l1) {
            System.out.println("*Stats*");
            System.out.println("\tint: " + i1);
            System.out.println("\tfloat: " + f1);
            System.out.println("\tdouble: " + d1);
            System.out.println("\tlong: " + l1);
        }
    }
}
