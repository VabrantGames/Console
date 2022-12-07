package com.vabrant.console.test.guitests;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.Input.Keys;
import com.vabrant.console.ConsoleCommand;
import com.vabrant.console.shortcuts.ShortcutManager;

public class ShortcutManagerKeyTest extends ApplicationAdapter {

    public static void main(String[] args) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setWindowedMode(300, 300);
        config.setTitle("ShortcutManagerTest");
        new Lwjgl3Application(new ShortcutManagerKeyTest(), config);
    }

    @Override
    public void create() {
        ShortcutManager manager = new ShortcutManager();
        manager.add(new int[]{Keys.CONTROL_LEFT, Keys.SHIFT_LEFT, Keys.O}, new PrintCommand("Hello Space"));
        Gdx.input.setInputProcessor(manager);
    }

    private static class PrintCommand implements ConsoleCommand {

        final String str;

        PrintCommand(String str) {
           this.str = str;
        }

        @Override
        public void execute() {
            System.out.println(str);
        }
    }
}
