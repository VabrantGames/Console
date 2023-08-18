
package com.vabrant.console.test.guitests;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.ScreenUtils;
import com.vabrant.console.commandextension.CommandCache;
import com.vabrant.console.commandextension.CommandExtension;
import com.vabrant.console.gui.DefaultGUIConsole;
import com.vabrant.console.gui.GUIConsole;
import com.vabrant.console.gui.shortcuts.DefaultKeyMap;
import com.vabrant.console.log.LogLevel;
import com.vabrant.console.test.TestMethods;
import com.vabrant.console.test.GUITestLauncher.WindowSize;

import static com.vabrant.console.gui.shortcuts.KeyMap.asArray;

@WindowSize(width = 1080, height = 720)
public class CommandExtensionTest extends ApplicationAdapter {

	private GUIConsole console;

	@Override
	public void create () {
		Skin skin = new Skin(Gdx.files.classpath("defaultskin/tinted/tinted.json"));

		console = new DefaultGUIConsole(null, skin, null);

		CommandExtension extension = new CommandExtension();
		extension.init(console);

		CommandCache cache = new CommandCache();
		cache.addShortcut( () -> System.out.println("Hello Cache"), new int[] {Keys.NUM_9});
		cache.addAll(new TestMethods(), "Test");
		extension.getData().setConsoleCache(cache);

		DefaultKeyMap keyMap = console.getKeyMap();
		keyMap.add( () -> System.out.println("Hello Extension"), asArray(Keys.CONTROL_LEFT, Keys.NUM_1));
		keyMap.add( () -> System.out.println("Hello Extension"), asArray(Keys.NUM_1));
		keyMap.add( () -> console.getLogManager().add("Test", "Hello", LogLevel.DEBUG), asArray(Keys.NUM_9));

		Gdx.input.setInputProcessor(console.getInput());
	}

	@Override
	public void resize (int width, int height) {
		console.resize(width, height);
	}

	@Override
	public void render () {
		ScreenUtils.clear(Color.WHITE);
		console.draw();
	}
}
