
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
import com.vabrant.console.log.LogManager;

public class GUIConsole extends Console {

	public static final String FOCUS_EVENT = "focus";
	public static final String UNFOCUS_EVENT = "unfocus";

	protected String consoleViewName;
	protected ObjectMap<String, View<?>> views;
	private Stage stage;
	protected DefaultKeyMap keyMap;
	private GUIConsoleKeyMap guiConsoleKeyMap;
	public ShortcutManager shortcutManager;
	private InputMultiplexer inputMultiplexer;
	private Queue<FocusObject> focusStack;
	private ConsoleScope currentScope;
	protected LogManager logManager;
	protected EventManager eventManager;

	public GUIConsole () {
		this(null, null, null);
	}

	public GUIConsole (Batch batch) {
		this(batch, null, null);
	}

	public GUIConsole (Batch batch, Skin skin, GUIConsoleConfiguration settings) {
		if (batch == null) {
			stage = new Stage(new ScreenViewport());
		} else {
			stage = new Stage(new ScreenViewport(), batch);
		}

		stage.addListener(new ViewFocusListener());

		if (skin != null) {
			VisUI.load(skin);
		} else {
			VisUI.load();
		}

		if (settings == null) {
			settings = new GUIConsoleConfiguration();
		}

		eventManager = new EventManager(FOCUS_EVENT, UNFOCUS_EVENT);
		logManager = new LogManager();
		views = new ObjectMap<>();
		focusStack = new Queue<>();
		keyMap = new DefaultKeyMap(ShortcutManager.GLOBAL_SCOPE);
		guiConsoleKeyMap = new GUIConsoleKeyMap();
		guiConsoleKeyMap.setConsoleKeyMap(keyMap);
		shortcutManager = new ShortcutManager();
		shortcutManager.setGUIConsole(this);
		shortcutManager.setKeyMap(guiConsoleKeyMap);
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
// consoleView.setActivePanel(logPanel.getName());
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

		eventManager.subscribe(FOCUS_EVENT, (FocusObjectListener)focusObject -> {
			guiConsoleKeyMap.setFocusKeyMap(focusObject.getKeyMap());
		});

		eventManager.subscribe(UNFOCUS_EVENT, (FocusObjectListener)focusObject -> {
			guiConsoleKeyMap.setFocusKeyMap(null);
		});
	}

	public EventManager getEventManager () {
		return eventManager;
	}

	public DefaultKeyMap getKeyMap () {
		return keyMap;
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

	public ConsoleScope getScope () {
		if (focusStack.size == 0 || getFocusObject().getScope() == null) return ShortcutManager.GLOBAL_SCOPE;
		return getFocusObject().getScope();
	}

	public boolean isScopeActive (ConsoleScope scope) {
		return getScope().equals(scope);
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

	/** Adds a shortcut to the keymap
	 * @param command
	 * @param keybind
	 * @return packed keybind */
	public Shortcut addShortcut (ConsoleCommand command, int[] keybind) {
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

	public boolean focus (FocusObject newFocusObject) {
		if (newFocusObject == null) return false;

		FocusObject activeFocusObject = getFocusObject();

		if (activeFocusObject != null) {
			if (activeFocusObject.equals(newFocusObject)) return false;

			// Keep track of the stack, even though they can't be focused
			if (activeFocusObject.lockFocus()) {
				int idx = focusStack.indexOf(newFocusObject, false);
				if (idx > -1) {
					focusStack.removeIndex(idx);
				}

				focusStack.removeLast();
				focusStack.addLast(newFocusObject);
				focusStack.addLast(activeFocusObject);

				return false;
			}

			unfocusFocusObject(activeFocusObject);
		}

		// If a FocusObject is being refocused remove it from the stack to be placed on top
		int idx = focusStack.indexOf(newFocusObject, false);
		if (idx > -1) focusStack.removeIndex(idx);

		focus0(newFocusObject);
		focusStack.addLast(newFocusObject);
		return true;
	}

	private void focus0 (FocusObject object) {
		object.focus();
		eventManager.fire(FOCUS_EVENT, object);
		logger.info("Focused FocusObject '" + object.getName() + "'");
	}

	private void unfocusFocusObject (FocusObject object) {
		object.unfocus();
		stage.setKeyboardFocus(null);
		stage.setScrollFocus(null);
		eventManager.fire(UNFOCUS_EVENT, object);
		logger.info("Unfocused FocusObject '" + object.getName() + "'");
	}

	public void removeFocusObject (FocusObject object) {
		if (object == null) return;

		boolean focusLast = false;
		FocusObject activeFocusObject = getFocusObject();

		if (activeFocusObject != null && activeFocusObject.equals(object)) {
			unfocusFocusObject(activeFocusObject);
			focusLast = true;
		}

		boolean removed = focusStack.removeValue(object, false);

		if (removed) {
			logger.info("Removed FocusObject " + object.getName());
		} else {
			logger.error("Could not remove FocusObject " + object.getName());
		}

		if (focusLast && focusStack.size > 0) {
			focus0(getFocusObject());
		}
	}

	public FocusObject getFocusObject () {
		if (focusStack.size == 0) return null;
		return focusStack.last();
	}

	public void addView (View<?> view) {
		if (views.containsKey(view.getName())) {
			throw new RuntimeException("View with name '" + view.getName() + "' already exists");
		}

		views.put(view.getName(), view);
		view.setStage(stage);
		view.setConsole(this);
	}

	public View getView (String name) {
		return views.get(name);
	}

	public Values<View<?>> getViews () {
		return views.values();
	}

	public static class GUIConsoleKeyMap implements KeyMap {

		private KeyMap consoleKeyMap;
		private KeyMap focusKeyMap;

		void setConsoleKeyMap (KeyMap keyMap) {
			consoleKeyMap = keyMap;
		}

		void setFocusKeyMap (KeyMap keyMap) {
			focusKeyMap = keyMap;
		}

		public KeyMap getConsoleKeyMap () {
			return consoleKeyMap;
		}

		public KeyMap getFocusKeyMap () {
			return focusKeyMap;
		}

		@Override
		public Shortcut getShortcut (int keybindPacked) {
			Shortcut shortcut = consoleKeyMap.getShortcut(keybindPacked);
			if (shortcut == null && focusKeyMap != null) {
				shortcut = focusKeyMap.getShortcut(keybindPacked);
			}
			return shortcut;
		}
	}

	private class ViewFocusListener extends InputListener {

		@Override
		public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
			View<?> view = null;
			for (View<?> v : getViews()) {
				if (v.isHidden() || !v.hit(x, y)) continue;
				if (view == null) {
					view = v;
				} else {
					if (v.getRootTable().getZIndex() > view.getRootTable().getZIndex()) {
						view = v;
					}
				}
			}

			if (view != null) {
				view.focus();
				return true;
			}

			return false;
		};
	}

	public interface FocusObjectListener extends EventListener<FocusObject> {

	}

}
