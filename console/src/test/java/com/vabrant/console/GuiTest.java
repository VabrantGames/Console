package com.vabrant.console;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class GuiTest extends ApplicationAdapter {
	
	public static void main(String[] args) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = 960;
		config.height = 640;
		 new LwjglApplication(new GuiTest(), config);
	}

	Console console;
	ConsoleCache cache;
	SpriteBatch batch;

	@Override
	public void create() {
		super.create();
		
		batch = new SpriteBatch();
		console = new Console(batch, new Skin(Gdx.files.internal("orangepeelui/uiskin.json")));
		cache = new ConsoleCache();
		cache.setLogLevel(DebugLogger.DEBUG);
		
//		cache.add(new ElectricGuitar(), "bass");
		cache.add(new Print(), "pr");
		cache.addReference(MathUtils.class, "mu");
		cache.addMethod(Print.class, "hello");
		cache.addReference(new Object(), "oo");
		console.setCache(cache);
		
//		console.add("acoustic", new Guitar());
//		console.add("print", new PrintPrimitives());
	}

	@Override
	public void resize(int width, int height) {
		console.resize(width, height);
	}
	
	@Override
	public void render() {
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		console.update(Gdx.graphics.getDeltaTime());
		console.draw();
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void dispose() {
		batch.dispose();
	}

}
