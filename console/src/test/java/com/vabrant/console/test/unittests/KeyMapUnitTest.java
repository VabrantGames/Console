
package com.vabrant.console.test.unittests;

import com.badlogic.gdx.Input.Keys;
import com.vabrant.console.ConsoleCommand;
import com.vabrant.console.gui.KeyMap;
import com.vabrant.console.gui.ShortcutManager;
import com.vabrant.console.test.ConsoleTestsUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class KeyMapUnitTest {

	public static KeyMap keyMap;
	private static ConsoleCommand command;

	@BeforeAll
	public static void init () {
		command = () -> System.out.println("Hello World");
	}

	@BeforeEach
	public void initTest () {
		keyMap = new KeyMap("test");
	}

	@Test
	void ChangeConsoleCommandTest() {
		ConsoleCommand newCommand = () -> System.out.println("World Hello");
		int packed = keyMap.add(command, Keys.NUM_1);

		keyMap.changeConsoleCommand(packed, newCommand);

		assertEquals(newCommand, keyMap.getShortcut(packed).getConsoleCommand());
	}

	@Test
	void ChangeTest() {
		int[] oldKeybind = new int[] {Keys.CONTROL_LEFT, Keys.NUM_5};
		int[] newKeybind = new int[] {Keys.HOME};
		int oldPacked = keyMap.add(command, oldKeybind);

		keyMap.changeKeybind(oldPacked, newKeybind);

		assertFalse(keyMap.hasKeybind(oldPacked));
		assertTrue(keyMap.hasKeybind(newKeybind));
	}

	@Test
	void HasAndGetTest () {
		int[] keybind = new int[] {Keys.NUM_1};
		int packed = keyMap.add(command, keybind);

		assertTrue(keyMap.hasKeybind(keybind));
		assertTrue(keyMap.hasKeybind(packed));
        assertNotNull(keyMap.getShortcut(keybind));
        assertNotNull(keyMap.getShortcut(packed));
	}

	@Test
	void RemoveTest() {
		int[] keybind = new int[] {Keys.NUM_1};
		int packed = keyMap.add(command, keybind);

		assertTrue(keyMap.removeKeybind(packed));
		assertFalse(keyMap.hasKeybind(packed));

		keyMap.add(command, keybind);
		assertTrue(keyMap.removeKeybind(keybind));
		assertFalse(keyMap.hasKeybind(keybind));
	}

	@Test
	void AddTest () {
		int[] keybind = new int[] {Keys.NUM_1, Keys.CONTROL_LEFT};
		int packed = keyMap.add( () -> System.out.println("Hello World"), keybind);

		assertTrue(keyMap.hasKeybind(packed));

		// Can't add a keybind twice
		assertThrows(RuntimeException.class, () -> keyMap.add( () -> System.out.println("Hello World"), keybind));

		// Null consoleCommand
		assertThrows(RuntimeException.class, () -> keyMap.add(null, new int[] {Keys.NUM_1}));

		// Null keybind
		assertThrows(RuntimeException.class, () -> keyMap.add( () -> System.out.println("Hello World")));
	}

	@Test
	void IsValidKeybindTest() {
//		ShortcutManager manager = new ShortcutManager();
		int[] valid1 = new int[] {Keys.SHIFT_LEFT, Keys.O};
		int[] valid2 = new int[] {Keys.SHIFT_LEFT, Keys.CONTROL_RIGHT, Keys.T};

		// All modifiers
		int[] invalid1 = new int[] {Keys.CONTROL_LEFT, Keys.CONTROL_RIGHT, Keys.ALT_LEFT};

		// More than one normal key
		int[] invalid2 = new int[] {Keys.O, Keys.P};

		// More than one of the same modifier
		int[] invalid3 = new int[] {Keys.CONTROL_LEFT, Keys.CONTROL_RIGHT, Keys.G};

		assertDoesNotThrow(
			() -> ConsoleTestsUtils.executePrivateMethod(keyMap, "isValidKeybind", new Class[] {int[].class}, valid1));
		assertDoesNotThrow(
			() -> ConsoleTestsUtils.executePrivateMethod(keyMap, "isValidKeybind", new Class[] {int[].class}, valid2));
		assertThrows(RuntimeException.class,
			() -> ConsoleTestsUtils.executePrivateMethod(keyMap, "isValidKeybind", new Class[] {int[].class}, invalid1));
		assertThrows(RuntimeException.class,
			() -> ConsoleTestsUtils.executePrivateMethod(keyMap, "isValidKeybind", new Class[] {int[].class}, invalid2));
		assertThrows(RuntimeException.class,
			() -> ConsoleTestsUtils.executePrivateMethod(keyMap, "isValidKeybind", new Class[] {int[].class}, invalid3));

	}

}
