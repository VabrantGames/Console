package com.vabrant.console;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class ConsoleTestApplication implements ApplicationListener {
	
	public static void main(String[] args) {
		new LwjglApplication(new ConsoleTestApplication(), getDefaultConfiguration());
	}
	
	private static LwjglApplicationConfiguration getDefaultConfiguration() {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "ConsoleTest";
		config.width = 960;
		config.height = 640;
		return config;
	}

	SpriteBatch batch;
	AssetManager assetManager;
	Console console;
	Viewport viewport;
	Texture pixelTexture;

	@Override
	public void create() {
		assetManager = new AssetManager();
		batch = new SpriteBatch();
		
		Pixmap pixel = new Pixmap(1, 1, Format.RGBA8888);
		pixel.setColor(Color.WHITE);
		pixel.drawPixel(0,0);
		pixelTexture = new Texture(pixel);
		pixel.dispose();
		
		ConsoleSettings settings = new ConsoleSettings();
		settings.batch = batch;
		
		Object one = new Object();
		Object two = new Object();
		
		System.out.println("Are equals? " + one.equals(two));
		
		console = new Console(settings);
		console.add("bass", new ElectricGuitar());
		console.add("acoustic", new Guitar());
		console.add("print", new PrintPrimitives());
		console.add("prippt", new PrintPrimitives());
		console.add("prin2", new PrintPrimitives());
		console.add("prin3", new PrintPrimitives());
		console.add("prin4", new PrintPrimitives());
		console.add("prin44", new PrintPrimitives());
		console.add("prin5", new PrintPrimitives());
		console.add("prin66", new PrintPrimitives());
		console.add("prin7", new PrintPrimitives());
		console.add("prin8", new PrintPrimitives());
		console.add("prin55", new PrintPrimitives());
		console.add("pri5", new PrintPrimitives());
		console.add("pri5t", new PrintPrimitives());
		console.add("pt", new PrintPrimitives());
		console.add("iint", new PrintPrimitives());
		console.printAllCommandObjects();
		console.printAllCommandMethods();
		console.printMethods2();
		viewport = new ExtendViewport(480, 320);
	}
	
	public void bob(double d) {
		System.out.println("num: " + d);
	}

	@Override
	public void resize(int width, int height) {
		viewport.update(width, height, true);
	}

	@Override
	public void render() {
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		console.update(Gdx.graphics.getDeltaTime());
		
		batch.setProjectionMatrix(viewport.getCamera().combined);
		batch.enableBlending();
		batch.begin();
		console.draw();
		console.debug();
		batch.end();
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
		assetManager.dispose();
		pixelTexture.dispose();
	}

}
