
package com.vabrant.console.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.StringBuilder;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.vabrant.console.Console;
import com.vabrant.console.ConsoleCache;
import com.vabrant.console.ConsoleCommand;
import com.vabrant.console.ExecutionStrategy;

public class GUIConsole extends Console {

	private boolean isHidden;
	private boolean isGUICache;

	private int hideShowKeybindPacked;
	private int executeCommandKeybindPacked;
	private int toggleCommandKeybindPacked;
	private Stage stage;
	private Table rootTable;
	StringBuilder builder;
	private ShortcutManager shortcutManager;
	private CommandLine commandLine;
	private ConsoleInputMultiplexer inputMultiplexer;
	private CloseWhenTouchedOutsideBounds closeWhenTouchedOutsideBounds;
	private ConsoleScope scope = ConsoleScope.DEFAULT;

	public GUIConsole () {
		this(null, new Skin(Gdx.files.classpath("orangepeelui/uiskin.json")));
	}

	public GUIConsole (Batch batch) {
		this(batch, new Skin(Gdx.files.classpath("orangepeelui/uiskin.json")));
	}

	public GUIConsole (Batch batch, Skin skin) {

		if (batch == null) {
			stage = new Stage(new ScreenViewport());
		} else {
			stage = new Stage(new ScreenViewport(), batch);
		}

		builder = new StringBuilder();
		commandLine = new CommandLine(commandExecutionData, this, skin);
		shortcutManager = new ShortcutManager();
		shortcutManager.setGUIConsole(this);
		shortcutManager.subscribeToExecutedEvent(commandLine.getShortcutEventListener());
		inputMultiplexer = new ConsoleInputMultiplexer(this);
		closeWhenTouchedOutsideBounds = new CloseWhenTouchedOutsideBounds();

		toggleCommandKeybindPacked = shortcutManager.add(new int[] {Input.Keys.GRAVE}, new ToggleConsoleCommand(this),
			ConsoleScope.GLOBAL);
		executeCommandKeybindPacked = shortcutManager.add(new int[] {Input.Keys.ENTER}, new ExecuteCommandCommand(this),
			ConsoleScope.COMMAND_LINE);

		rootTable = new Table(skin);
		rootTable.setFillParent(true);
		rootTable.pad(4);
		rootTable.add(commandLine).expand().fillX().bottom();
		stage.addActor(rootTable);

		setHidden(true);

		inputMultiplexer.add(closeWhenTouchedOutsideBounds);
		inputMultiplexer.add(shortcutManager);
		inputMultiplexer.add(commandLine.getInput());
	}

	public ConsoleScope getScope () {
		return scope;
	}

	@Override
	public void setCache (ConsoleCache cache) {
		super.setCache(cache);

		boolean setupInput = false;

		if (getCache() != null && isGUICache) {
			isGUICache = false;
			GUIConsoleCache cs = ((GUIConsoleCache)getCache());
			ShortcutManager sm = cs.getShortcutManager();
			sm.setGUIConsole(null);
			sm.unsubscribeFromExecutedEvent(commandLine.getShortcutEventListener());
			inputMultiplexer.clear();
			setupInput = true;
		}

		if (cache == null) return;

		if (cache instanceof GUIConsoleCache) {
			isGUICache = true;
			ShortcutManager cacheShortcutManager = ((GUIConsoleCache)cache).getShortcutManager();
			cacheShortcutManager.setGUIConsole(this);
			cacheShortcutManager.subscribeToExecutedEvent(commandLine.getShortcutEventListener());
			inputMultiplexer.clear();
			inputMultiplexer.add(closeWhenTouchedOutsideBounds);
			inputMultiplexer.add(shortcutManager);
			inputMultiplexer.add(cacheShortcutManager);
			inputMultiplexer.add(commandLine.getInput());
		} else {
			if (!setupInput) return;
			inputMultiplexer.add(closeWhenTouchedOutsideBounds);
			inputMultiplexer.add(shortcutManager);
			inputMultiplexer.add(commandLine.getInput());
		}
	}

	public void setToggleKeybind (int[] keybind) {
		toggleCommandKeybindPacked = shortcutManager.replace(toggleCommandKeybindPacked, keybind);
	}

	public InputProcessor getInput () {
		return inputMultiplexer;
	}

	public Stage getStage () {
		return stage;
	}

	ShortcutManager getShortcutManager () {
		return shortcutManager;
	}

	CommandLine getCommandLine () {
		return commandLine;
	}

	/** Adds a global shortcut. Non-global shortcuts should be added to a {@link GUIConsoleCache}
	 *
	 * @param keybind
	 * @param command
	 * @return */
	public int addShortcut (int[] keybind, ConsoleCommand command) {
		return shortcutManager.add(keybind, command);
	}

	public int addShortcut (int[] keybind, ConsoleCommand command, ConsoleScope shortcutScope) {
		return shortcutManager.add(keybind, command, shortcutScope);
	}

	public void setHidden (boolean hidden) {
		if (isHidden() == hidden) return;
		isHidden = hidden;

		if (hidden) {
			scope = ConsoleScope.DEFAULT;
			rootTable.setTouchable(Touchable.disabled);
			rootTable.setVisible(false);
			stage.setKeyboardFocus(null);
		} else {
			scope = ConsoleScope.COMMAND_LINE;
			rootTable.setTouchable(Touchable.enabled);
			rootTable.setVisible(true);
			stage.setKeyboardFocus(commandLine);
		}
	}

	public boolean isHidden () {
		return isHidden;
	}

	public void resize (int width, int height) {
		stage.getViewport().update(width, height, true);
	}

	public void draw () {
		stage.act();

		if (isHidden()) return;

		stage.getViewport().apply();
		stage.draw();
	}

	private class CloseWhenTouchedOutsideBounds extends InputAdapter {
		@Override
		public boolean touchDown (int x, int y, int pointer, int button) {
			Actor actor = stage.hit(x, y, true);
			if (actor == null) return false;

			if (!actor.equals(commandLine)) {
				setHidden(true);
				return true;
			}

			return false;
		}
	}

}
