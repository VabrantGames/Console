package com.vabrant.console;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
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
		this(batch, new Skin(Gdx.files.classpath("orangepeelui/uiskin.json")));
	}

	public Console(Batch batch, Skin skin) {
		if(batch == null) throw new IllegalArgumentException("Batch is null");
		
		stage = new Stage(new ScreenViewport(), batch);
		this.skin = skin;
		
		Table root = new Table();
		
		root.setFillParent(true);
		root.setDebug(true);
		
		commandLine = new CommandLine(this, skin);
		
		root.add(commandLine).expandY().growX().bottom();
		stage.addActor(root);
		stage.setKeyboardFocus(commandLine);
		
		stage.addCaptureListener(new ClickListener() {
			boolean disabled = false;
			@Override
			public boolean keyDown(InputEvent event, int keycode) {
				if(Gdx.input.isKeyPressed(Keys.CONTROL_LEFT) && keycode == Keys.GRAVE) {
					disabled = !disabled;
					
					if(disabled) {
						root.setTouchable(Touchable.disabled);
						root.setVisible(false);
						stage.setKeyboardFocus(null);
					}
					else {
						root.setTouchable(Touchable.enabled);
						root.setVisible(true);
						stage.setKeyboardFocus(commandLine);
					}
				}
				return true;
			}
		});

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
		stage.getViewport().apply();
		stage.draw();
	}

}
