
package com.vabrant.console.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
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

public class DefaultGUIConsole implements GUIConsole {

	public static final String FOCUS_EVENT = "focus";
	public static final String UNFOCUS_EVENT = "unfocus";

	protected String consoleViewName;
	protected ObjectMap<String, View> views;
	private Stage stage;
	protected DefaultKeyMap keyMap;
	protected KeyMapReference panelKeyMapReference;
	private KeyMapMultiplexer keyMapMultiplexer;
	public ShortcutManager shortcutManager;
	private InputMultiplexer inputMultiplexer;
	private Queue<FocusObject> focusStack;
	protected LogManager logManager;
	protected EventManager eventManager;
	protected Skin skin;
	protected DebugLogger logger;
	protected DefaultConsole console;

	public DefaultGUIConsole () {
		this(null, null, null);
	}

	public DefaultGUIConsole (Batch batch) {
		this(batch, null, null);
	}

	public DefaultGUIConsole (Batch batch, Skin skn, GUIConsoleConfiguration config) {
		console = new DefaultConsole();
		logger = console.getLogger();
		logger.setName(this.getClass());

		if (batch == null) {
			stage = new Stage(new ScreenViewport());
		} else {
			stage = new Stage(new ScreenViewport(), batch);
		}

		stage.addListener(new ViewFocusListener());

		if (skn == null) {
			skn = new Skin(Gdx.files.classpath("defaultskin/tinted/tinted.json"));
		}

		Skin loadedSkin = VisUI.isLoaded() ? VisUI.getSkin() : null;
		VisUI.dispose(false);
		VisUI.load(skn);

		this.skin = skn;

		if (config == null) {
			config = new GUIConsoleConfiguration();
		}

		eventManager = new EventManager(FOCUS_EVENT, UNFOCUS_EVENT);
		logManager = new LogManager();
		views = new ObjectMap<>();
		focusStack = new Queue<>();
		keyMap = new DefaultKeyMap(ShortcutManager.GLOBAL_SCOPE);
		panelKeyMapReference = new KeyMapReference<>();
		keyMapMultiplexer = new KeyMapMultiplexer();
		keyMapMultiplexer.add(keyMap);
		keyMapMultiplexer.add(panelKeyMapReference);
		shortcutManager = new ShortcutManager();
		shortcutManager.setGUIConsole(this);
		shortcutManager.setKeyMap(keyMapMultiplexer);
		inputMultiplexer = new InputMultiplexer();
		inputMultiplexer.addProcessor(shortcutManager);
		inputMultiplexer.addProcessor(stage);

		if (config.createConsoleView) {
			consoleViewName = config.consoleViewName;

			if (consoleViewName == null || consoleViewName.isEmpty()) {
				throw new ConsoleRuntimeException("Invalid console view name");
			}

			View consoleView;

			Panel logPanel = new LogPanel("ConsoleLog", logManager, skin);

			if (config.customConsoleView == null) {
				DefaultView<?> v = new WindowView(consoleViewName, skin, logPanel);
				v.setWidthPercent(config.consoleViewWidthPercent);
				v.setHeightPercent(config.consoleViewHeightPercent);
				v.moveToTop();
				consoleView = v;
			} else {
				consoleView = config.customConsoleView;
			}

			addView(consoleView);

			if (config.showConsoleView) {
				consoleView.setHidden(false);
			}

			if (config.toggleConsoleViewKeybind != null) {
				keyMap.add(new ToggleViewVisibilityCommand(consoleView, true), config.toggleConsoleViewKeybind);
			}

			if (config.closeAllViewsKeybind != null) {
				keyMap.add(new CloseAllViewsCommand(this), config.closeAllViewsKeybind);
			}

			if (loadedSkin != null) {
				VisUI.dispose(false);
				VisUI.load(loadedSkin);
			}
		} else {
			consoleViewName = "";
		}

		eventManager.subscribe(FOCUS_EVENT, (FocusObjectListener)focusObject -> {
			if (focusObject instanceof Panel) {
				panelKeyMapReference.setReference(focusObject.getKeyMap());
			}
		});

		eventManager.subscribe(UNFOCUS_EVENT, (FocusObjectListener)focusObject -> {
			panelKeyMapReference.setReference(null);
		});
	}

	public DebugLogger getLogger () {
		return logger;
	}

	@Override
	public Skin getSkin () {
		return skin;
	}

	@Override
	public EventManager getEventManager () {
		return eventManager;
	}

	@Override
	public DefaultKeyMap getKeyMap () {
		return keyMap;
	}

	@Override
	public KeyMapMultiplexer getKeyMapMultiplexer () {
		return keyMapMultiplexer;
	}

// public void setConsoleViewName (String name) {
// consoleViewName = name;
// }

	public String getConsoleViewName () {
		return consoleViewName;
	}

	@Override
	public View getConsoleView () {
		return getView(consoleViewName);
	}

	@Override
	public LogManager getLogManager () {
		return logManager;
	}

	@Override
	public ConsoleScope getScope () {
		if (focusStack.size == 0 || getFocusObject().getScope() == null) return ShortcutManager.GLOBAL_SCOPE;
		return getFocusObject().getScope();
	}

	@Override
	public boolean isScopeActive (ConsoleScope scope) {
		return getScope().equals(scope);
	}

	@Override
	public InputProcessor getInput () {
		return inputMultiplexer;
	}

	@Override
	public Stage getStage () {
		return stage;
	}

	@Override
	public ShortcutManager getShortcutManager () {
		return shortcutManager;
	}

	/** Adds a shortcut to the keymap
	 * @param command
	 * @param keybind
	 * @return packed keybind */
	public Shortcut addShortcut (ShortcutCommand command, int[] keybind) {
		return keyMap.add(command, keybind);
	}

	@Override
	public void resize (int width, int height) {
		float oldWidth = stage.getWidth();
		float oldHeight = stage.getHeight();
		stage.getViewport().update(width, height, true);
		for (View v : getViews()) {
			v.resize(oldWidth, oldHeight, width, height);
		}
	}

	@Override
	public void draw () {
		stage.act();
		stage.getViewport().apply();
		stage.draw();
	}

	@Override
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
		if (idx > -1) {
			focusStack.removeIndex(idx);
		}

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

	@Override
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

	@Override
	public FocusObject getFocusObject () {
		if (focusStack.size == 0) return null;
		return focusStack.last();
	}

	@Override
	public void addView (View view) {
		if (views.containsKey(view.getName())) {
			throw new ConsoleRuntimeException("View with name '" + view.getName() + "' already exists");
		}

		views.put(view.getName(), view);
		view.setConsole(this);
	}

	@Override
	public View getView (String name) {
		return views.get(name);
	}

	@Override
	public Values<View> getViews () {
		return views.values();
	}

	@Override
	public void addStrategy (String name, ConsoleStrategy<?> strategy) {
		console.addStrategy(name, strategy);
	}

	@Override
	public ConsoleStrategy getStrategy (String name) {
		return console.getStrategy(name);
	}

	@Override
	public boolean execute (Object o) {
		return console.execute(o);
	}

	private class ViewFocusListener extends InputListener {

		@Override
		public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
			View view = null;
			for (View v : getViews()) {
				if (v.isHidden() || !v.hit(x, y) || v.getZIndex() < 0) continue;
				if (view == null) {
					view = v;
				} else {
					if (v.getZIndex() > view.getZIndex()) {
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
