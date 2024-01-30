
package com.vabrant.console.test.unittests;

import com.badlogic.gdx.Input.Keys;
import com.vabrant.console.gui.shortcuts.Shortcut;
import com.vabrant.console.gui.DefaultKeyboardScope;
import com.vabrant.console.gui.shortcuts.DefaultKeyMap;
import com.vabrant.console.gui.shortcuts.ShortcutManager;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

public class DefaultKeyMapUnitTest {

	private static final String ID = "id";
	private static DefaultKeyboardScope scope;
	public static DefaultKeyMap keyMap;
	private static Runnable command;

	@BeforeAll
	public static void init () {
		command = () -> System.out.println("Hello World");

		scope = new DefaultKeyboardScope("Hello");
	}

	@BeforeEach
	public void initTest () {
		keyMap = new DefaultKeyMap(scope);
	}

	@Test
	void ChangeConsoleCommandTest () {
		Runnable newCommand = () -> System.out.println("World Hello");
		keyMap.register(ID, command, new int[] {Keys.NUM_1});

		keyMap.changeConsoleCommand(ID, newCommand);

		assertEquals(newCommand, keyMap.getShortcut(ID).getConsoleCommand());
	}

	@Test
	void ChangeTest () {
		int[] oldKeybind = new int[] {Keys.CONTROL_LEFT, Keys.NUM_5};
		int[] newKeybind = new int[] {Keys.HOME};

		keyMap.register(ID, command, oldKeybind);
		keyMap.changeKeybind(ID, newKeybind);

		assertTrue(Arrays.equals(keyMap.getShortcut(ID).getKeybind(), ShortcutManager.sortKeybind(newKeybind)));
	}

	@Test
	void RemoveTest () {
		int[] keybind = new int[] {Keys.NUM_1};
		keyMap.register(ID, command, keybind);

		// Clear keybind
		assertTrue(keyMap.changeKeybind(ID, null));

		assertFalse(keyMap.hasKeybind(keybind));

		keyMap.unregister(ID);

		assertFalse(keyMap.hasShortcut(ID));
	}

	@Test
	void AddTest () {
		Shortcut s = keyMap.register(ID, () -> System.out.println("Hello World"), null);

		assertTrue(keyMap.hasShortcut(ID));
		assertTrue(keyMap.hasKeybind(s.getKeybindPacked()));
	}

}
