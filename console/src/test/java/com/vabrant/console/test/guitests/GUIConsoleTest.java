package com.vabrant.console.test.guitests;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.vabrant.console.ConsoleCache;
import com.vabrant.console.GUIConsole;
import com.vabrant.console.annotation.ConsoleMethod;
import com.vabrant.console.annotation.ConsoleObject;
import com.vabrant.console.executionstrategy.SimpleExecutionStrategy;

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
        Skin skin = new Skin(Gdx.files.internal("orangepeelui/uiskin.json"));
//        Skin skin = new Skin(Gdx.files.internal("commodore64ui/uiskin.json"));

//        Skin skin = new Skin(Gdx.files.internal("rustyrobotui/rusty-robot-ui.json"));
//		Skin skin = new Skin(Gdx.files.internal("quantumhorizonui/quantum-horizon-ui.json"));

//        console = new GUIConsole(new SimpleExecutionStrategy(), skin);
        console = new GUIConsole();

        cache = new ConsoleCache();
        cache.add(this, "test");

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
    public void hello() {
        System.out.println("Hello Console");
    }
}
