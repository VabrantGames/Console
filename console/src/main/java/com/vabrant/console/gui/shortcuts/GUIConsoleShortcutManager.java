
package com.vabrant.console.gui.shortcuts;

import com.badlogic.gdx.InputProcessor;
import com.vabrant.console.events.DefaultEvent;
import com.vabrant.console.events.EventListener;
import com.vabrant.console.events.EventManager;
import com.vabrant.console.gui.KeyboardScope;
import com.vabrant.console.gui.GUIConsole;

import java.util.Arrays;

public class GUIConsoleShortcutManager extends ShortcutManager implements InputProcessor {
	// ========== Guide ==========//
	// 0 = Control
	// 1 = Shift
	// 2 = Alt
	// 3 = key

	public static final KeyboardScope GLOBAL_SCOPE = new KeyboardScope("global");

	public static final String EXECUTED_EVENT = "executed";
	private GUIConsole console;
	private final EventManager eventManager;
	private ShortcutManagerFilter filter;
	private EventListener<GUIConsoleExecutedShortcutEvent> executedCommandListener;
	private GUIConsoleExecutedShortcutEvent shortcutManagerEvent;

	public GUIConsoleShortcutManager (EventManager eventManager) {
		this.eventManager = eventManager;
		shortcutManagerEvent = new GUIConsoleExecutedShortcutEvent();

		if (eventManager != null) {
			eventManager.addEvent(GUIConsoleExecutedShortcutEvent.class);
		}
	}

//	public void subscribeToEvent (String event, EventListener<ShortcutManagerEvent> listener) {
//		eventManager.subscribe(event, listener);
//	}
//
//	public void subscribeToExecutedEvent (EventListener<ShortcutManagerEvent> listener) {
//		eventManager.subscribe(EXECUTED_EVENT, listener);
//	}
//
//	public void unsubscribeFromExecutedEvent (EventListener<ShortcutManagerEvent> listener) {
//		eventManager.unsubscribe(EXECUTED_EVENT, listener);
//	}

	public void setGUIConsole (GUIConsole console) {
		this.console = console;
	}

	public void setKeycodeFilter (ShortcutManagerFilter filter) {
		this.filter = filter;
	}

	public void setExecutedCommandListener (EventListener<GUIConsoleExecutedShortcutEvent> listener) {
		executedCommandListener = listener;
	}

	@Override
	public boolean keyDown (int keycode) {
		// Modifier keys have to be pressed before the normal key
		// ctrl -> shift -> o != o -> ctrl -> shift

		if (pressedKeys[3] != 0 || keyMap == null) return false;

		if (setKey(pressedKeys, keycode)) {
			pressedKeysPacked = packKeybindSorted(pressedKeys);
		}

		if (filter != null) {
			shortcutManagerEvent.clear().setKeybind(pressedKeys).setPackedKeybind(pressedKeysPacked)
				.setConsoleScope(console.getKeyboardScope());
			if (!filter.acceptKeycodeTyped(shortcutManagerEvent, keycode)) return false;
		}

		Shortcut shortcut = keyMap.getShortcut(pressedKeysPacked);

		if (shortcut == null) return false;

		KeyboardScope shortcutScope = shortcut.getScope();

		if (!shortcutScope.equals(GLOBAL_SCOPE) && !console.isScopeActive(shortcutScope)) return false;

		shortcut.getConsoleCommand().execute();

		GUIConsoleExecutedShortcutEvent context = new GUIConsoleExecutedShortcutEvent();
		context.setKeybind(pressedKeys);
		context.setPackedKeybind(pressedKeysPacked);

		shortcutManagerEvent.clear().setKeybind(pressedKeys).setPackedKeybind(pressedKeysPacked);

		if (executedCommandListener != null) executedCommandListener.handleEvent(context);
		if (eventManager != null) {
			eventManager.fire(GUIConsoleExecutedShortcutEvent.class, shortcutManagerEvent);
		}
		return true;
	}

	@Override
	public boolean keyTyped (char character) {
		return false;
	}

	@Override
	public boolean touchDown (int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchUp (int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchDragged (int screenX, int screenY, int pointer) {
		return false;
	}

	@Override
	public boolean mouseMoved (int screenX, int screenY) {
		return false;
	}

	@Override
	public boolean scrolled (float amountX, float amountY) {
		return false;
	}

	public interface ShortcutManagerFilter {
		boolean acceptKeycodeTyped (GUIConsoleExecutedShortcutEvent context, int keycode);
	}

	public static class GUIConsoleExecutedShortcutEvent extends DefaultEvent {

		private int[] keybind;
		private int keybindPacked;
		private KeyboardScope keyboardScope;
		private Shortcut shortcut;

		GUIConsoleExecutedShortcutEvent () {
			keybind = new int[MAX_KEYS];
		}

		GUIConsoleExecutedShortcutEvent setKeybind (int[] keybind) {
			for (int i = 0; i < MAX_KEYS; i++) {
				this.keybind[i] = keybind[i];
			}
			return this;
		}

		public int[] getKeybind () {
			return keybind;
		}

		GUIConsoleExecutedShortcutEvent setPackedKeybind (int keybindPacked) {
			this.keybindPacked = keybindPacked;
			return this;
		}

		public int getKeybindPacked () {
			return keybindPacked;
		}

		GUIConsoleExecutedShortcutEvent setConsoleScope (KeyboardScope scope) {
			keyboardScope = scope;
			return this;
		}

		public KeyboardScope getConsoleScope () {
			return keyboardScope;
		}

		public boolean isModifierKeyPressed () {
			return keybind[0] != 0 || keybind[1] != 0 || keybind[2] != 0;
		}

		GUIConsoleExecutedShortcutEvent setShortcut (Shortcut shortcut) {
			this.shortcut = shortcut;
			return this;
		}

		public Shortcut getShortcut () {
			return shortcut;
		}

		GUIConsoleExecutedShortcutEvent clear () {
			Arrays.fill(keybind, 0);
			keybindPacked = 0;
			keyboardScope = null;
			shortcut = null;
			return this;
		}
	}

}
