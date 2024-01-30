
package com.vabrant.console.gui.shortcuts;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.utils.FocusListener;
import com.badlogic.gdx.utils.DelayedRemovalArray;

public class ActorKeyMap {

	private KeyboardFocusListener keyboardFocusListener;
	private ActorShortcutManager shortcutManager;
	private DefaultKeyMap keyMap;

	public ActorKeyMap (Actor actor) {
		keyboardFocusListener = new KeyboardFocusListener();
		shortcutManager = new ActorShortcutManager();

		DelayedRemovalArray listeners = actor.getListeners();
		listeners.insert(0, keyboardFocusListener);
		listeners.insert(1, shortcutManager);
	}

	private class ActorShortcutManager extends InputListener {

		private ShortcutManager manager;

		private ActorShortcutManager () {
			manager = new ShortcutManager();
		}

		private void clearPressedKeys () {
			manager.clearPressedKeys();
		}

		@Override
		public boolean keyUp (InputEvent event, int keycode) {
			manager.keyUp(keycode);
			return false;
		}

		@Override
		public boolean keyDown (InputEvent event, int keycode) {
			return manager.keyDown(keycode);
		}
	}

	private class KeyboardFocusListener extends FocusListener {
		@Override
		public void keyboardFocusChanged (FocusEvent event, Actor actor, boolean focused) {
			if (!focused) {
				shortcutManager.clearPressedKeys();
			}
		}
	}
}
