package com.vabrant.console.test.unittests;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.vabrant.console.ConsoleCache;
import com.vabrant.console.annotation.ConsoleMethod;
import com.vabrant.console.annotation.ConsoleObject;
import com.vabrant.console.parsers.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ParseTests {

    private static ConsoleCacheAndStringInput data;
    private static Application application;

    @BeforeAll
    public static void init() {
        application = new HeadlessApplication(new ApplicationAdapter() {
        });
        data = new ConsoleCacheAndStringInput();
    }

    @Test
    void FloatTest() {
        FloatArgumentParser parser = new FloatArgumentParser();
        assertDoesNotThrow(() -> parser.parse(data.setText("15f")));
        assertDoesNotThrow(() -> parser.parse(data.setText(".15f")));
        assertDoesNotThrow(() -> parser.parse(data.setText("15.0f")));
    }

    @Test
    void IntTest() {
        IntArgumentParser parser = new IntArgumentParser();
        assertDoesNotThrow(() -> parser.parse(data.setText("100")));
        assertDoesNotThrow(() -> parser.parse(data.setText("0x64")));
        assertDoesNotThrow(() -> parser.parse(data.setText("#64")));
        assertDoesNotThrow(() -> parser.parse(data.setText("0b01100100")));
    }

    @Test
    void DoubleTest() {
        DoubleArgumentParser parser = new DoubleArgumentParser();
        assertDoesNotThrow(() -> parser.parse(data.setText("100.0")));
        assertDoesNotThrow(() -> parser.parse(data.setText("100.0d")));
        assertDoesNotThrow(() -> parser.parse(data.setText(".0d")));
    }

    @Test
    void LongTest() {
        LongArgumentParser parser = new LongArgumentParser();
        assertDoesNotThrow(() -> parser.parse(data.setText("100l")));
        assertDoesNotThrow(() -> parser.parse(data.setText("100L")));
    }

    @Test
    void InstanceReferenceTest() {
        Object ob1 = new Object();
        Object ob2 = new Object();
        InstanceReferenceParser arg = new InstanceReferenceParser();
        ConsoleCache cache = new ConsoleCache();
        cache.addReference(ob1, "ob1");
        cache.addReference(ob2, "ob2");

        data.setConsoleCache(cache);

        assertDoesNotThrow(() -> arg.parse(data.setText("ob1")));
        assertDoesNotThrow(() -> arg.parse(data.setText("ob2")));

        data.setConsoleCache(null);
    }

    @Test
    void StringTest() {
        StringArgumentParser parser = new StringArgumentParser();

        String str1 = "Bob";
        String str2 = "\"" + str1 + "\"";
        assertEquals(str1, parser.parse(data.setText(str1)));
        assertEquals(str1, parser.parse(data.setText(str2)));
    }

    @Test
    void MethodTest() {
        ConsoleCache cache = new ConsoleCache();
        MethodArgumentParser arg = new MethodArgumentParser();

        data.setConsoleCache(cache);

        @ConsoleObject
        class Bob {
            @ConsoleMethod
            public void hello() {
            }
        }
        Bob bob = new Bob();

        cache.add(bob, "bob");

        assertDoesNotThrow(() -> arg.parse(data.setText(".hello")));

        data.setConsoleCache(null);
    }

    @Test
    void BooleanTest() {
        ConsoleCache cache = new ConsoleCache();
        BooleanArgumentParser parser = new BooleanArgumentParser();

        data.setConsoleCache(cache);
        assertDoesNotThrow(() -> parser.parse(data.setText("true")));
        assertDoesNotThrow(() -> parser.parse(data.setText("false")));
        assertDoesNotThrow(() -> parser.parse(data.setText("TRUE")));
		assertFalse(parser.parse(data.setText("fjowefjow")));
    }
}
