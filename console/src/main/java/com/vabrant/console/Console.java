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

public class Console extends Table{
	
	Skin skin;
	Stage stage;
	
	public final DebugLogger logger = new DebugLogger(Console.class, DebugLogger.DEBUG);
	private final TextBox textBox;
	private final Batch batch;

	public Console(Batch batch) {
		//TODO REMOVE! FOR DEBUGGING ONLY!
		Gdx.app.setLogLevel(Application.LOG_DEBUG);
		
		if(batch == null) throw new IllegalArgumentException("Batch is null");
		
		this.batch = batch;
		stage = new Stage(new ScreenViewport(), batch);
		skin = new Skin(Gdx.files.internal("orangepeelui/uiskin.json"));
//		skin = new Skin(Gdx.files.internal("rustyrobotui/rusty-robot-ui.json"));
//		skin = new Skin(Gdx.files.internal("quantumhorizonui/quantum-horizon-ui.json"));
		setFillParent(true);
		setDebug(true);
		
		textBox = new TextBox(this);
		
		stage.addActor(this);
		stage.setKeyboardFocus(textBox);
		
//		Table textBoxTable = new Table();
//		textBoxTable.setDebug(true);
//		textBoxTable.add(textBox).
//		textBoxTable.add(textBox).prefHeight(30).minHeight(30).expandY().growX().bottom();

		this.setDebug(true);
		textBox.setSize(this.add(textBox));
//		this.add(textBoxTable).prefHeight(30).grow();
//		this.add(textBox).height(30).expandY().growX().bottom();
		
		Gdx.input.setInputProcessor(new InputMultiplexer(ConsoleShortcuts.instance, stage));
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
