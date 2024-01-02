
package com.vabrant.console.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectMap.Values;
import com.badlogic.gdx.utils.Queue;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.kotcrab.vis.ui.VisUI;
import com.vabrant.console.*;
import com.vabrant.console.events.Event;
import com.vabrant.console.events.EventListener;
import com.vabrant.console.events.EventManager;
import com.vabrant.console.gui.commands.CloseAllViewsCommand;
import com.vabrant.console.gui.commands.ToggleViewVisibilityCommand;
import com.vabrant.console.gui.events.GUIConsoleFocusEvent;
import com.vabrant.console.gui.events.GUIConsoleUnfocusEvent;
import com.vabrant.console.gui.shortcuts.*;
import com.vabrant.console.gui.views.CommandLineView;
import com.vabrant.console.gui.views.DefaultView;
import com.vabrant.console.gui.views.View;
import com.vabrant.console.log.LogManager;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class DefaultGUIConsole implements GUIConsole {

	protected String consoleViewName;
	protected ObjectMap<String, View> views;
	private Stage stage;
	protected DefaultKeyMap globalKeyMap;
	private ParentKeyMap parentKeyMap;
	public GUIConsoleShortcutManager shortcutManager;
	private InputMultiplexer inputMultiplexer;
	private Queue<FocusObject> focusStack;
	protected LogManager logManager;
	protected EventManager eventManager;
	protected Skin skin;
	protected DebugLogger logger;
	protected DefaultConsole console;
	protected CommandLineView commandLineView;
	protected ShapeDrawer shapeDrawer;
	protected GUIConsoleFocusEvent focusEvent;
	protected GUIConsoleUnfocusEvent unfocusEvent;

	public DefaultGUIConsole () {
		this(null, null, null);
	}

	public DefaultGUIConsole (Batch batch) {
		this(batch, null, null);
	}

	public DefaultGUIConsole (Batch batch, Skin skn, GUIConsoleConfiguration config) {
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

		console = new DefaultConsole();
		eventManager = console.getEventManager();
		logger = console.getLogger();
		logger.setName(this.getClass());

		Pixmap pix = new Pixmap(1, 1, Format.RGBA8888);
		pix.drawPixel(0, 0, 0xFFFFFFFF);
		shapeDrawer = new ShapeDrawer(batch != null ? batch : stage.getBatch(), new TextureRegion(new Texture(pix)));
		pix.dispose();

		eventManager.addEvents(GUIConsoleFocusEvent.class, GUIConsoleUnfocusEvent.class);
		focusEvent = new GUIConsoleFocusEvent();
		unfocusEvent = new GUIConsoleUnfocusEvent();

		logManager = new LogManager(100, eventManager);
		views = new ObjectMap<>();
		focusStack = new Queue<>();
		globalKeyMap = new DefaultKeyMap(GUIConsoleShortcutManager.GLOBAL_SCOPE);
		parentKeyMap = new ParentKeyMap(globalKeyMap);
		shortcutManager = new GUIConsoleShortcutManager(eventManager);
		shortcutManager.setGUIConsole(this);
		shortcutManager.setKeyMap(parentKeyMap);
		inputMultiplexer = new InputMultiplexer();
		inputMultiplexer.addProcessor(shortcutManager);
		inputMultiplexer.addProcessor(stage);

		commandLineView = new CommandLineView("CommandLine", skin, shapeDrawer);
		addView(commandLineView);
		Shortcut s = globalKeyMap.add(new ToggleViewVisibilityCommand(commandLineView),  Keys.GRAVE);
		commandLineView.setToggleViewShortcut(s);

		if (config.closeAllViewsKeybind != null) {
				s = globalKeyMap.add(new CloseAllViewsCommand(this), config.closeAllViewsKeybind);
				commandLineView.setCloseAllViewsShortcut(s);
		}

		if (config.createConsoleView) {
			consoleViewName = config.consoleViewName;

			if (consoleViewName == null || consoleViewName.isEmpty()) {
				throw new ConsoleRuntimeException("Invalid console view name");
			}

			DefaultView consoleView = null;

//			PanelView logPanel = new LogPanel("ConsoleLog", logManager, skin);

			if (config.customConsoleView == null) {
//				PanelManagerView<?> v = new WindowView(consoleViewName, skin, logPanel);
//				v.setWidthPercent(config.consoleViewWidthPercent);
//				v.setHeightPercent(config.consoleViewHeightPercent);
//				v.moveToTop();
//				consoleView = v;
			} else {
				consoleView = config.customConsoleView;
			}

//			addView(consoleView);

			if (config.showConsoleView) {
//				consoleView.setHidden(false);
			}

			if (config.toggleConsoleViewKeybind != null) {
//				globalKeyMap.add(new ToggleViewVisibilityCommand(consoleView, true), config.toggleConsoleViewKeybind);
			}



			if (loadedSkin != null) {
				VisUI.dispose(false);
				VisUI.load(loadedSkin);
			}
		} else {
			consoleViewName = "";
		}
	}

	@Override
	public <T extends Event> void subscribeToEvent (Class<T> event, EventListener<T> listener) {
		eventManager.subscribe(event, listener);
	}

	@Override
	public <T extends Event> boolean unsubscribeFromEvent (Class<T> event, EventListener<T> listener) {
		return eventManager.unsubscribe(event, listener);
	}

	@Override
	public <T extends Event> void fireEvent (Class<T> type, T event) {
		eventManager.fire(type, event);
	}

	@Override
	public <T extends Event> void postFireEvent (Class<T> type, T event) {
		eventManager.postFire(type, event);
	}

	public ShapeDrawer getShapeDrawer() {
		return shapeDrawer;
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
		return globalKeyMap;
	}

	@Override
	public void setActiveExtension (ConsoleExtension extension) {
		console.setActiveExtension(extension);
	}

	@Override
	public ConsoleExtension getActiveExtension () {
		return console.getActiveExtension();
	}

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
	public KeyboardScope getKeyboardScope () {
		if (focusStack.size == 0 || getFocusObject().getKeyboardScope() == null) return GUIConsoleShortcutManager.GLOBAL_SCOPE;
		return getFocusObject().getKeyboardScope();
	}

	@Override
	public boolean isScopeActive (KeyboardScope scope) {
		KeyboardScope currentActiveScope = getKeyboardScope();

		if (currentActiveScope == null) return false;

		if (currentActiveScope instanceof ParentKeyboardScope) {
			ParentKeyboardScope parentScope = (ParentKeyboardScope) currentActiveScope;
			KeyboardScope childScope = parentScope.getChildScope();

			if (parentScope.equals(scope) || childScope != null && childScope.equals(scope)) return true;
		}

		return currentActiveScope.equals(scope);
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
	public GUIConsoleShortcutManager getShortcutManager () {
		return shortcutManager;
	}

	/** Adds a shortcut to the keymap
	 * @param command
	 * @param keybind
	 * @return packed keybind */
	@Override
	public Shortcut addGlobalShortcut (ShortcutCommand command, int... keybind) {
		return globalKeyMap.add(command, keybind);
	}

	@Override
	public Shortcut addShortcut (KeyboardScope scope, ShortcutCommand command, int... keys) {
		return globalKeyMap.add(scope, command, keys);
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
		ScreenUtils.clear(Color.WHITE);
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
		parentKeyMap.setChild(object.getKeyMap());
		focusEvent.setFocusObject(object);
		eventManager.postFire(GUIConsoleFocusEvent.class, focusEvent);
		logger.info("Focused FocusObject '" + object.getName() + "'");
	}

	private void unfocusFocusObject (FocusObject object) {
		object.unfocus();
		stage.setKeyboardFocus(null);
		stage.setScrollFocus(null);
		eventManager.fire(GUIConsoleUnfocusEvent.class, unfocusEvent);
		logger.info("Unfocused FocusObject '" + object.getName() + "'");
	}

	@Override
	public boolean isFocused (FocusObject focusObject) {
		return getFocusObject().equals(focusObject);
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
		view.setGUIConsole(this);
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
	public void addExtension (String name, ConsoleExtension strategy) {
		console.addExtension(name, strategy);
	}

	@Override
	public ConsoleExtension getExtension (String name) {
		return console.getExtension(name);
	}

	@Override
	public boolean execute (Object o) {
		return console.execute(o);
	}

	@Override
	public boolean execute (ConsoleExtension extension, Object input) {
		return console.execute(extension, input);
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
				focus(view);
				return true;
			}

			return false;
		};
	}

}
