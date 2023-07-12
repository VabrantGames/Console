
package com.vabrant.console.gui;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectMap.Values;
import com.badlogic.gdx.utils.Queue;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.kotcrab.vis.ui.VisUI;
import com.vabrant.console.*;
import com.vabrant.console.gui.commands.CloseAllViewsCommand;
import com.vabrant.console.gui.commands.ToggleViewVisibilityCommand;
import com.vabrant.console.gui.panels.LogPanel;
import com.vabrant.console.gui.shortcuts.*;
import com.vabrant.console.log.LogLevel;
import com.vabrant.console.log.LogManager;

public class GUIConsole extends Console {

	private View<?> focusedView;
	protected String consoleViewName;
	protected ObjectMap<String, View<?>> views;
	private Stage stage;
	protected DefaultKeyMap keyMap;
	private GUIConsoleKeyMap keyMapMultiplexer;
	public ShortcutManager shortcutManager;
	private InputMultiplexer inputMultiplexer;
	private Queue<ConsoleScope> scopeStack;
	protected LogManager logManager;
	private final DebugLogger logger;

	public GUIConsole () {
		this(null, null, null);
	}

	public GUIConsole (Batch batch) {
		this(batch, null, null);
	}

	public GUIConsole (Batch batch, Skin skin, GUIConsoleConfiguration settings) {
		super(settings);

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

		if (settings == null) {
			settings = new GUIConsoleConfiguration();
		}

		logManager = new LogManager();
		views = new ObjectMap<>();
		logger = new DebugLogger(this.getClass().getSimpleName(), DebugLogger.NONE);
		scopeStack = new Queue<>();
		keyMap = new DefaultKeyMap(ShortcutManager.GLOBAL_SCOPE);
		keyMapMultiplexer = new GUIConsoleKeyMap();
		keyMapMultiplexer.setConsoleKeyMap(keyMap);
		shortcutManager = new ShortcutManager();
		shortcutManager.setGUIConsole(this);
		shortcutManager.setKeyMap(keyMapMultiplexer);
		inputMultiplexer = new InputMultiplexer();
		inputMultiplexer.addProcessor(shortcutManager);
		inputMultiplexer.addProcessor(stage);

		if (settings.createConsoleView) {
			consoleViewName = settings.consoleViewName;
			View<?> consoleView;

			if (settings.customConsoleView == null) {
				Panel logPanel = new LogPanel("ConsoleLog", logManager);
				switch (settings.consoleViewType) {
				case TABLE:
					consoleView = new TableView(consoleViewName, logPanel);
					break;
				case WINDOW:
					consoleView = new WindowView(consoleViewName, logPanel);
					break;
				case MULTI_PANEL_WINDOW:
					MultiPanelWindowView v = new MultiPanelWindowView(consoleViewName);
					v.addPanel(logPanel);
					consoleView = v;
					break;
				default:
					throw new RuntimeException("Invalid console view type");
				}
			} else {
				consoleView = settings.customConsoleView;
			}

			addView(consoleView);

			if (settings.showConsoleView) {
				consoleView.setHidden(false);
			}

			consoleView.setWidthPercent(settings.consoleViewWidthPercent);
			consoleView.setHeightPercent(settings.consoleViewHeightPercent);

			if (settings.toggleConsoleViewKeybind != null) {
				keyMap.add(new ToggleViewVisibilityCommand(consoleView, true), settings.toggleConsoleViewKeybind);
			}

			if (settings.closeAllViewsKeybind != null) {
				keyMap.add(new CloseAllViewsCommand(this), settings.closeAllViewsKeybind);
			}
		}
	}

	@Override
	public void log (String tag, String message, LogLevel level) {
		super.log(tag, message, level);
		logManager.add(tag, message, level);
	}

	public void setConsoleViewName (String name) {
		consoleViewName = name;
	}

	public String getConsoleViewName () {
		return consoleViewName;
	}

	public LogManager getLogManger () {
		return logManager;
	}

	public DebugLogger getLogger () {
		return logger;
	}

	public ConsoleScope getScope () {
		if (scopeStack.size == 0) return ShortcutManager.GLOBAL_SCOPE;
		return scopeStack.last();
	}

	public boolean isScopeActive (ConsoleScope scope) {
		return getScope().equals(scope);
	}

	void setScope (ConsoleScope scope) {
		ConsoleScope cs = getScope();

		if (cs != null && cs.equals(scope)) return;

		int idx = -1;
		for (int i = 0; i < scopeStack.size; i++) {
			ConsoleScope c = scopeStack.get(i);

			if (scope.equals(c)) {
				idx = i;
				break;
			}
		}

		if (idx > -1) {
			scopeStack.removeIndex(idx);
		}

		logger.debug("Set scope '" + scope.getName() + "'");
		scopeStack.addLast(scope);
	}

	public void removeScope (ConsoleScope scope) {
		if (scopeStack.size == 0) return;
		boolean removed = scopeStack.removeValue(scope, false);

		if (removed) {
			logger.debug("Removed scope '" + scope.getName() + "'");
		}
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

	/** Adds a shortcut to the keymap
	 * @param command
	 * @param keybind
	 * @return packed keybind */
	public Shortcut addShortcut (ConsoleCommand command, int... keybind) {
		return keyMap.add(command, keybind);
	}

	public void resize (int width, int height) {
		stage.getViewport().update(width, height, true);
	}

	public void draw () {
		stage.act();
		stage.getViewport().apply();
		stage.draw();
	}

	boolean resetFocus (View<?> view) {
		if (focusedView != view) return false;
		focusedView = null;
		stage.setKeyboardFocus(null);
		stage.setScrollFocus(null);
		return true;
	}

	boolean focusView (View<?> view) {
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

		view.subscribeToEvent(View.FOCUS_EVENT, new EventListener<View>() {
			@Override
			public void handleEvent (View view) {
				Panel panel = view.getPanel();
				keyMapMultiplexer.panelKeyMap = panel.getKeyMap();
			}
		});

		view.subscribeToEvent(View.UNFOCUS_EVENT, new EventListener<View>() {
			@Override
			public void handleEvent (View view) {
				keyMapMultiplexer.panelKeyMap = null;
			}
		});
	}

	public Values<View<?>> getViews () {
		return views.values();
	}

	private class GUIConsoleKeyMap implements KeyMap {

		private KeyMap consoleKeyMap;
		private KeyMap panelKeyMap;

		void setConsoleKeyMap (KeyMap keyMap) {
			consoleKeyMap = keyMap;
		}

		void setPanelKeyMap (KeyMap keyMap) {
			panelKeyMap = keyMap;
		}

		public KeyMap getConsoleKeyMap () {
			return consoleKeyMap;
		}

		public KeyMap getPanelKeyMap () {
			return panelKeyMap;
		}

		@Override
		public Shortcut getShortcut (int keybindPacked) {
			Shortcut shortcut = consoleKeyMap.getShortcut(keybindPacked);
			if (shortcut == null && panelKeyMap != null) {
				shortcut = panelKeyMap.getShortcut(keybindPacked);
			}
			return shortcut;
		}
	}

	private class FocusViewListener extends InputListener {

		@Override
		public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
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
