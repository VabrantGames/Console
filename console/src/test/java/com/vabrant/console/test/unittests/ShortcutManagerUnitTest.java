
package com.vabrant.console.test.unittests;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.vabrant.console.gui.shortcuts.ShortcutManager;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ShortcutManagerUnitTest {

	private static ShortcutManager manager;
	private static Application application;

	@BeforeAll
	public static void init () {
		application = new HeadlessApplication(new ApplicationAdapter() {});
	}

	@BeforeEach
	public void setup () {
		manager = new ShortcutManager();
	}

// @Test
	void ExecuteTest () {
// GUIConsole console = new GUIConsole();
// KeyMap map = new KeyMap("test");
// map.add(() -> System.out.println("Hello World"), Keys.NUM_1);
// ConsoleTestsUtils.executePrivateMethod(manager, "setConsoleKeyMap", new Class[] {KeyMap.class}, map);
// ConsoleTestsUtils.executePrivateMethod(manager, "setGUIConsole", new Class[] {GUIConsole.class}, console);

// manager.addKeyMap(map);

// assertTrue(manager.keyDown(Keys.NUM_1));
	}

	@Test
	void globalScopeTest () {

// assertThrows(RuntimeException.class,
// () -> manager.add(new int[] {Keys.A}, () -> System.out.println("Hello"), ConsoleScope.GLOBAL));
// assertThrows(RuntimeException.class,
// () -> manager.add(new int[] {Keys.NUM_0}, () -> System.out.println("Hello"), ConsoleScope.GLOBAL));
// assertDoesNotThrow( () -> manager.add(new int[] {Keys.HOME}, () -> System.out.println("Hello"), ConsoleScope.GLOBAL));
	}

}
