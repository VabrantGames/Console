package com.vabrant.console.test.guitests;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.graphics.GL20;
import com.vabrant.console.gui.GUIConsole;

public class SecondaryInputTest extends ApplicationAdapter {

    public static void main(String[] args) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setWindowedMode(960, 640);
        config.setTitle("SecondaryInputTest");
        new Lwjgl3Application(new SecondaryInputTest(), config);
    }

    private GUIConsole console;

    @Override
    public void create() {
        console = new GUIConsole();

        InputMultiplexer multi = new InputMultiplexer();
        multi.addProcessor(console.getInput());
        multi.addProcessor(new Input());
        Gdx.input.setInputProcessor(multi);
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        console.draw();
    }

    private class Input extends InputAdapter {

        @Override
        public boolean keyTyped(char character) {
            System.out.println(character);
           return true;
        }

        @Override
        public boolean keyDown(int keycode) {
            return false;
        }
    }
}
