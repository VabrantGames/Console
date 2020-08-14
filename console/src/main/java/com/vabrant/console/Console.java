package com.vabrant.console;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.vabrant.console.shortcuts.ConsoleShortcuts;

public class Console {
	
	private Skin skin;
	private final Stage stage;
	public final DebugLogger logger = new DebugLogger(Console.class, DebugLogger.DEBUG);
	private final CommandLine commandLine;
	private ConsoleCache cache;
	private ConsoleCache globalCache;

	public Console(Batch batch) {
		//TODO REMOVE! FOR DEBUGGING ONLY!
		Gdx.app.setLogLevel(Application.LOG_DEBUG);
		
		if(batch == null) throw new IllegalArgumentException("Batch is null");
		
		stage = new Stage(new ScreenViewport(), batch);
		skin = new Skin(Gdx.files.classpath("orangepeelui/uiskin.json"));
//		skin = new Skin(Gdx.files.internal("rustyrobotui/rusty-robot-ui.json"));
//		skin = new Skin(Gdx.files.internal("quantumhorizonui/quantum-horizon-ui.json"));
		
		Table root = new Table();
		
		root.setFillParent(true);
		root.setDebug(true);
		
		commandLine = new CommandLine(this, skin);
		
		root.add(commandLine).expandY().growX().bottom();
		stage.addActor(root);
		stage.setKeyboardFocus(commandLine);

		root.setDebug(true);
		
		Gdx.input.setInputProcessor(new InputMultiplexer(ConsoleShortcuts.instance, stage));
	}
	
	public void setCache(ConsoleCache cache) {
		if(cache == null) throw new IllegalArgumentException("Cache is null.");
		this.cache = cache;
	}
	
	public ConsoleCache getCache() {
		return cache;
	}
	
	public void resize(int width, int height) {
		stage.getViewport().update(width, height, true);
	}

	public void update(float delta) {
		stage.act(delta);
	}
	
	public void draw() {
		stage.draw();
	}

}
