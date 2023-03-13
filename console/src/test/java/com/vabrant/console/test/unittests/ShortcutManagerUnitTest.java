package com.vabrant.console.test.unittests;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.Method;
import com.badlogic.gdx.utils.reflect.ReflectionException;
import com.vabrant.console.shortcuts.ShortcutManager;
import com.vabrant.console.test.ConsoleTestsUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class ShortcutManagerUnitTest {

    private static Application application;

    @BeforeAll
    public static void init() {
        application = new HeadlessApplication(new ApplicationAdapter() {
        });
    }

    @Test
    void isValidKeybindTest() {
        ShortcutManager manager = new ShortcutManager();
        int[] valid1 = new int[]{Keys.SHIFT_LEFT, Keys.O};
        int[] valid2 = new int[]{Keys.SHIFT_LEFT, Keys.CONTROL_RIGHT, Keys.T};

        //All modifiers
        int[] invalid1 = new int[]{Keys.CONTROL_LEFT, Keys.CONTROL_RIGHT, Keys.ALT_LEFT};

        //More than one normal key
        int[] invalid2 = new int[]{Keys.O, Keys.P};

        //More than one of the same modifier
        int[] invalid3 = new int[]{Keys.CONTROL_LEFT, Keys.CONTROL_RIGHT, Keys.G};

        assertDoesNotThrow(() -> ConsoleTestsUtils.executePrivateMethod(manager, "isValidKeybind", new Class[]{int[].class}, valid1));
        assertDoesNotThrow(() -> ConsoleTestsUtils.executePrivateMethod(manager, "isValidKeybind", new Class[]{int[].class}, valid2));
        assertThrows(RuntimeException.class, () -> ConsoleTestsUtils.executePrivateMethod(manager, "isValidKeybind", new Class[]{int[].class}, invalid1));
        assertThrows(RuntimeException.class, () -> ConsoleTestsUtils.executePrivateMethod(manager, "isValidKeybind", new Class[]{int[].class}, invalid2));
        assertThrows(RuntimeException.class, () -> ConsoleTestsUtils.executePrivateMethod(manager, "isValidKeybind", new Class[]{int[].class}, invalid3));

    }

    @Test
    void replaceTest() {
        ShortcutManager manager = new ShortcutManager();
        int oldKeybind = manager.add(new int[]{Keys.A}, () -> System.out.println("Hello"));

        assertTrue(manager.contains(oldKeybind));

        int newKeybind = manager.replace(oldKeybind, new int[]{Keys.S});

        assertFalse(manager.contains(oldKeybind));
        assertTrue(manager.contains(newKeybind));
    }


}
