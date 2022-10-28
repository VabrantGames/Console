package com.vabrant.console.test;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class ConsoleTestApplication extends InputAdapter implements ApplicationListener {
	
	public static int WIDTH;
	public static int HEIGHT;
	public static Class<? extends ConsoleTestApplication> TEST_APP_CLASS;

	public static void main(String[] args) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setWindowedMode(WIDTH, HEIGHT);
//		config.width = WIDTH;
//		config.height = HEIGHT;
		
		ConsoleTestApplication app = null;
		
		try {
			app = TEST_APP_CLASS.newInstance();
		}
		catch(IllegalAccessException | InstantiationException e) {
			e.printStackTrace();
			Gdx.app.exit();
		}
		
		new Lwjgl3Application(app, config);
	}
	
	public SpriteBatch batch;
	
	@Override
	public void create() {
		batch = new SpriteBatch();
	}

	@Override
	public void resize(int width, int height) {
	}

	public void update(float delta) {
	}
	
	public void draw(Batch batch) {
	}
	
	@Override
	public void render() {
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		update(Gdx.graphics.getDeltaTime());
		draw(batch);
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void dispose() {
	}

}
