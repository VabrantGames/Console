package com.vabrant.console.gui;

import com.vabrant.console.ConsoleCache;
import com.vabrant.console.ConsoleCommand;

public class GUIConsoleCache extends ConsoleCache {

    private ShortcutManager shortcutManager;

    public GUIConsoleCache() {
        shortcutManager = new ShortcutManager();
    }

    public ShortcutManager getShortcutManager() {
        return shortcutManager;
    }

    public int addShortcut(int[] keybind, ConsoleCommand command) {
        return shortcutManager.add(keybind, command);
    }

    public int addShortcut(int[] keybind, ConsoleCommand command, ConsoleScope scope) {
        return shortcutManager.add(keybind, command, scope);
    }
}
