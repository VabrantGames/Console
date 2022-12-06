package com.vabrant.console.test.guitests;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Logger;
import com.vabrant.console.ConsoleCache;
import com.vabrant.console.gui.GUIConsole;
import com.vabrant.console.annotation.ConsoleMethod;
import com.vabrant.console.annotation.ConsoleObject;

@ConsoleObject
public class GUIConsoleTest extends ApplicationAdapter {

    public static void main(String[] args) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setWindowedMode(960, 640);
        config.setTitle("GUIConsoleTest");
        new Lwjgl3Application(new GUIConsoleTest(), config);
    }

    private GUIConsole console;
    private ConsoleCache cache;

    @Override
    public void create() {
        console = new GUIConsole();
        console.logToSystem(true);
        cache = new ConsoleCache();
        cache.setLogLevel(Logger.DEBUG);
        cache.add(this, "obj");
        console.setCache(cache);
        Gdx.input.setInputProcessor(console.getInput());
    }

    @Override
    public void resize(int width, int height) {
        console.resize(width, height);
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        console.draw();
    }

    @ConsoleMethod
    public void print(String str) {
        System.out.println(str);
    }

    @ConsoleMethod
    public void hello() {
        System.out.println("Hello Console");
    }

    @ConsoleMethod
    public void helloBoolean(boolean b) {

    }
}
