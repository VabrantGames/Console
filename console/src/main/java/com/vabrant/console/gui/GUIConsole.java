
package com.vabrant.console.gui;

import com.badlogic.gdx.*;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectMap.Entry;
import com.badlogic.gdx.utils.ObjectMap.Values;
import com.badlogic.gdx.utils.StringBuilder;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.kotcrab.vis.ui.VisUI;
import com.vabrant.console.Console;
import com.vabrant.console.ConsoleCache;
import com.vabrant.console.ConsoleCommand;
import com.vabrant.console.commandstrategy.gui.CommandLinePanel;

public class GUIConsole extends Console {

	private boolean isGUICache;

	private View<?> focusedView;
	private ObjectMap<String, View<?>> views;
	private Stage stage;
	StringBuilder builder;
	protected KeyMap keyMap;
	public ShortcutManager shortcutManager;
	private InputMultiplexer inputMultiplexer;
	private String scope = "";

	public GUIConsole () {
		this(null, null);
	}

	public GUIConsole (Batch batch) {
		this(batch, null);
	}

	public GUIConsole (Batch batch, Skin skin) {
		if (batch == null) {
			stage = new Stage(new ScreenViewport());
		} else {
			stage = new Stage(new ScreenViewport(), batch);
		}

		stage.addListener(new FocusViewListener());

		if (skin != null) {
			VisUI.load(skin);
		} else {
			VisUI.load();
		}

		views = new ObjectMap<>();

		builder = new StringBuilder();
		keyMap = new KeyMap(ShortcutManager.GLOBAL_SCOPE);
		shortcutManager = new ShortcutManager();
		shortcutManager.setGUIConsole(this);
		shortcutManager.setConsoleKeyMap(keyMap);
		inputMultiplexer = new InputMultiplexer();

		inputMultiplexer.addProcessor(shortcutManager);
		inputMultiplexer.addProcessor(stage);
	}
	public String getScope() {
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
// sm.unsubscribeFromExecutedEvent(commandLine.getShortcutEventListener());
			inputMultiplexer.clear();
			setupInput = true;
		}

		if (cache == null) return;

		if (cache instanceof GUIConsoleCache) {
			isGUICache = true;
			shortcutManager.setCacheKeyMap(((GUIConsoleCache) cache).getKeyMap());
			inputMultiplexer.clear();

			inputMultiplexer.addProcessor(shortcutManager);
			inputMultiplexer.addProcessor(stage);
// inputMultiplexer.add(commandLine.getInput());
		} else {
			if (!setupInput) return;
// inputMultiplexer.add(closeWhenTouchedOutsideBounds);
			inputMultiplexer.addProcessor(shortcutManager);
			inputMultiplexer.addProcessor(stage);
// inputMultiplexer.add(commandLine.getInput());
		}
	}

	void setScope(String scope) {
		this.scope = scope;
	}

	public InputProcessor getInput () {
		return inputMultiplexer;
	}

	public Stage getStage () {
		return stage;
	}

	public ShortcutManager getShortcutManager () {
		return shortcutManager;
	}

	public View getFocusedView () {
		return focusedView;
	}

	CommandLine getCommandLine () {
		return null;
// return commandLine;
	}

	/**
	 * Adds a
	 * @param command
	 * @param keybind
	 * @return
	 */
	public int addShortcut(ConsoleCommand command, int... keybind) {
		return keyMap.add(command, keybind);
	}

	/** Adds a global shortcut. Non-global shortcuts should be added to a {@link GUIConsoleCache}
	 *
	 * @param keybind
	 * @param command
	 * @return */
	public int addShortcut (int[] keybind, ConsoleCommand command) {
		return 0;
//		return shortcutManager.add(keybind, command);
	}

	public int addShortcut (int[] keybind, ConsoleCommand command, ConsoleScope shortcutScope) {
		return 0;
//		return shortcutManager.add(keybind, command, shortcutScope);
	}

// public void setHidden (boolean hidden) {
// if (isHidden() == hidden) return;
// isHidden = hidden;
//
// if (hidden) {
// scope = ConsoleScope.DEFAULT;
// rootTable.setTouchable(Touchable.disabled);
// rootTable.setVisible(false);
// stage.setKeyboardFocus(null);
// } else {
// scope = ConsoleScope.COMMAND_LINE;
// rootTable.setTouchable(Touchable.enabled);
// rootTable.setVisible(true);
// stage.setKeyboardFocus(commandLine);
// }
// }

	public boolean isHidden () {
// return isHidden;
		return false;
	}

	public void resize (int width, int height) {
		stage.getViewport().update(width, height, true);
	}

	public void draw () {
		stage.act();
		stage.getViewport().apply();
		stage.draw();
	}

	boolean resetFocus(View<?> view) {
		if (focusedView != view) return false;
		focusedView = null;
		stage.setKeyboardFocus(null);
		stage.setScrollFocus(null);
		return true;
	}

	boolean focusView(View<?> view) {
		if (focusedView != null && focusedView.equals(view)) return false;
		focusedView = view;
		stage.setKeyboardFocus(null);
		stage.setScrollFocus(null);
		return true;
	}

	public void addView (View<?> view) {
		if (views.containsKey(view.getName())) {
			throw new RuntimeException("View with name '" + view.getName() + "' already exists");
		}
		views.put(view.getName(), view);
		view.setStage(stage);
		view.setConsole(this);
	}

	public Values<View<?>> getViews() {
		return views.values();
	}

	private class FocusViewListener extends InputListener {

		@Override
		public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
			for (View<?> v : getViews()) {
					if (v.isHidden() || !v.hit(x, y)) continue;
					if (focusedView != null && focusedView.equals(v)) break;
					if (focusedView != null) focusedView.unfocus();

					v.focus();
					return true;
				}
			return false;
		};
	}


}
