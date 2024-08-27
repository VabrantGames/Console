
package com.vabrant.console.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.Queue;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.vabrant.console.*;
import com.vabrant.console.events.Event;
import com.vabrant.console.events.EventListener;
import com.vabrant.console.events.EventManager;
import com.vabrant.console.gui.commands.CloseAllViewsCommand;
import com.vabrant.console.gui.commands.ToggleViewVisibilityCommand;
import com.vabrant.console.gui.events.GUIConsoleFocusEvent;
import com.vabrant.console.gui.events.GUIConsoleUnfocusEvent;
import com.vabrant.console.gui.shortcuts.*;
import com.vabrant.console.gui.views.*;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class DefaultGUIConsole extends DefaultConsole implements GUIConsole {

	public static String TOGGLE_COMMANDLINE_SHORTCUT_ID = "Open Commandline";
	public static String TOGGLE_CONSOLE_VIEW_SHORTCUT_ID = "Open Console View";
	public static String CLOSE_ALL_VIEWS_SHORTCUT_ID = "Close All Views";

	protected String consoleViewName;
	protected ObjectMap<String, View> views;
	private Stage stage;
	protected DefaultKeyMap globalKeyMap;
	private ParentKeyMap parentKeyMap;
	public GUIConsoleShortcutManager shortcutManager;
	private InputMultiplexer inputMultiplexer;
	private Queue<FocusObject> focusStack;
	protected Skin skin;
	protected CommandLineView commandLineView;
	protected DefaultViewManager consoleViewManager;
	protected ShapeDrawer shapeDrawer;
	protected GUIConsoleFocusEvent focusEvent;
	protected GUIConsoleUnfocusEvent unfocusEvent;

	public DefaultGUIConsole () {
		this(null, null, null);
	}

	public DefaultGUIConsole (Batch batch) {
		this(batch, null, null);
	}

	public DefaultGUIConsole (Batch batch, Skin skn, DefaultGUIConsoleConfiguration config) {
		super(config == null ? new DefaultGUIConsoleConfiguration() : config);

		if (batch == null) {
			stage = new Stage(new ScreenViewport());
		} else {
			stage = new Stage(new ScreenViewport(), batch);
		}

		stage.addListener(new ViewFocusListener());

		if (skn == null) {
			skn = new Skin(Gdx.files.classpath("defaultskin/tinted/tinted.json"));
		}

		this.skin = skn;

		if (config == null) {
			config = new DefaultGUIConsoleConfiguration();
		}

		logger.setName(this.getClass());

		Pixmap pix = new Pixmap(1, 1, Format.RGBA8888);
		pix.drawPixel(0, 0, 0xFFFFFFFF);
		shapeDrawer = new ShapeDrawer(batch != null ? batch : stage.getBatch(), new TextureRegion(new Texture(pix)));
		pix.dispose();

		eventManager.registerEvents(GUIConsoleFocusEvent.class, GUIConsoleUnfocusEvent.class);
		focusEvent = new GUIConsoleFocusEvent();
		unfocusEvent = new GUIConsoleUnfocusEvent();

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

		Shortcut shortcutTemp = globalKeyMap.register(CLOSE_ALL_VIEWS_SHORTCUT_ID, new CloseAllViewsCommand(this),
			config.closeAllViewsKeybind);

		commandLineView = new CommandLineView("CommandLine", skin, shapeDrawer);
		addView(commandLineView);
		commandLineView.setCloseAllViewsShortcut(shortcutTemp);
		shortcutTemp = globalKeyMap.register(TOGGLE_COMMANDLINE_SHORTCUT_ID, new ToggleViewVisibilityCommand(commandLineView),
			config.toggleCommandLineKeybind);
		commandLineView.setToggleViewShortcut(shortcutTemp);

		if (config.createConsoleView) {
			consoleViewName = config.consoleViewName;

			if (consoleViewName == null || consoleViewName.isEmpty()) {
				throw new ConsoleRuntimeException("Invalid console view name");
			}

			consoleViewManager = new DefaultViewManager(config.consoleViewName, new Window("ConsoleView", skin), null, 10);
			consoleViewManager.setSizePercent(30, 80);
			consoleViewManager.setPosition(Utils.TOP_LEFT);
			consoleViewManager.translate(5, -5);
			shortcutTemp = globalKeyMap.register(TOGGLE_CONSOLE_VIEW_SHORTCUT_ID,
				new ToggleViewVisibilityCommand(consoleViewManager), config.toggleConsoleViewKeybind);
			addView(consoleViewManager);

			LogView logView = LogView.createTableView("Log", logManager, skin, shapeDrawer);
			consoleViewManager.addView(logView);

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

	public ShapeDrawer getShapeDrawer () {
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

	public View getCommandLineView () {
		return commandLineView;
	}

	public ViewManager getConsoleViewManager () {
		return consoleViewManager;
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
	public Shortcut addGlobalShortcut (String ID, Runnable command, int... keybind) {
		return globalKeyMap.register(ID, command, keybind);
	}

	@Override
	public Shortcut addShortcut (String ID, KeyboardScope scope, Runnable command, int... keybind) {
		return globalKeyMap.register(ID, scope, command, keybind);
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
			if (activeFocusObject.equals(newFocusObject) || activeFocusObject.lockFocus()) return false;
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
	public Array<View> getViews () {
		return views.values().toArray();
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
