
package com.vabrant.console.command.test.gui;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.ScreenUtils;
import com.vabrant.console.command.test.TestMethods;
import com.vabrant.console.gui.DefaultGUIConsole;
import com.vabrant.console.gui.GUIConsole;
import com.vabrant.console.guitestlauncher.GUITestLauncher.WindowSize;
import commandextension.ClassReference;
import commandextension.CommandCache;
import commandextension.CommandExtension;
import commandextension.DefaultCommandCache;

@WindowSize(width = 1080, height = 720)
public class CommandExtensionTest extends ApplicationAdapter {

	private CommandExtension extension;
	private GUIConsole console;

	@Override
	public void create () {
		Skin skin = new Skin(Gdx.files.classpath("defaultskin/tinted/tinted.json"));

		console = new DefaultGUIConsole(null, skin, null);
		extension = new CommandExtension();

		CommandCache cache = new DefaultCommandCache();
// cache.addShortcut( () -> System.out.println("Hello Cache"), new int[] {Keys.NUM_9});
		ClassReference<?> reference = cache.addReference(new TestMethods(), "test");
		cache.addAll(reference);

		ClassReference<?> mathUtilsReference = cache.addReference(MathUtils.class, "mu");
		cache.addCommand(mathUtilsReference, "random", int.class);

		extension.setConsoleCache(cache);

		console.addExtension(extension);
		console.setActiveExtension(extension);

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
