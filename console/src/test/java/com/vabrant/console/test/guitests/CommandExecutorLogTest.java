
package com.vabrant.console.test.guitests;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.vabrant.console.commandexecutor.CommandExecutorResult;
import com.vabrant.console.commandexecutor.ConsoleCommand;
import com.vabrant.console.commandexecutor.DefaultCommandCache;
import com.vabrant.console.ConsoleExtension;
import com.vabrant.console.DebugLogger;
import com.vabrant.console.gui.DefaultGUIConsole;
import com.vabrant.console.test.GUITestLauncher.WindowSize;

@WindowSize(width = 1080, height = 720)
public class CommandExecutorLogTest extends ApplicationAdapter {

	private DefaultGUIConsole console;

	@Override
	public void create () {
		super.create();
		console = new DefaultGUIConsole();

		TestExtension ext = new TestExtension();
		console.addExtension(ext);
		console.setActiveExtension(ext);

		Gdx.input.setInputProcessor(console.getInput());
	}

	@Override
	public void resize (int width, int height) {
		console.resize(width, height);
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		console.draw();
	}

	static class TestExtension extends ConsoleExtension {

		private DefaultCommandCache cache;

		TestExtension () {
			super("TestExt");

			cache = new DefaultCommandCache();
			cache.setLogLevel(DebugLogger.DEBUG);
			cache.addMethodCommand(cache.addReference("ref", this), "hello");
		}

		@ConsoleCommand(successMessage = "Hello")
		public void hello () {
		}

		@Override
		public boolean execute (Object o) throws Exception {
			CommandExecutorResult result = console.getCommandExecutor().execute(cache, "hello");

			if (!result.getExecutionStatus()) {
				System.out.println(result.getErrorString());
			}
			return true;
		}
	}
}
