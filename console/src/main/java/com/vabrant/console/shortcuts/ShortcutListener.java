package com.vabrant.console.shortcuts;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.utils.IntArray;

public class ShortcutListener extends InputListener {
	
	private ShortcutManager manager;
	
	public ShortcutListener(ShortcutManager manager) {
		this.manager = manager;
	}

	@Override
	public boolean keyDown(InputEvent event, int keycode) {
		ConsoleShortcuts shortcuts = ConsoleShortcuts.instance;
		boolean hasShortcut = manager.hasShortcut(shortcuts.getPackedKeys());
		
		if(hasShortcut) {
			manager.invoke();
//			shortcuts.clear();
		}
		
		return hasShortcut;
	}

}
