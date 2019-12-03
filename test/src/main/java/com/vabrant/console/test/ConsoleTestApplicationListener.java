package com.vabrant.console.test;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.vabrant.console.Console;

public class ConsoleTestApplicationListener implements ApplicationListener{

	SpriteBatch batch;
	AssetManager assetManager;
	Console console;
	Viewport viewport;
	ShapeRenderer shapeRenderer;
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

		
		assetManager.load(Console.FONT_TEXTURE_PATH, Texture.class);
		assetManager.finishLoading();
		
		console = new Console(batch, new TextureRegion(pixelTexture, 0, 0, 1, 1), assetManager);
		viewport = new ExtendViewport(480, 320);
		shapeRenderer = new ShapeRenderer();
		shapeRenderer.setAutoShapeType(true);
	}

	@Override
	public void resize(int width, int height) {
		viewport.update(width, height, true);
	}

	@Override
	public void render() {
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		batch.setProjectionMatrix(viewport.getCamera().combined);
		batch.enableBlending();
		batch.begin();
		console.draw(batch);
		batch.end();
		
//		Gdx.gl.glClearColor(1, 1, 1, 1);
//		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		Gdx.gl.glEnable(GL20.GL_BLEND);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		shapeRenderer.setProjectionMatrix(viewport.getCamera().combined);
		shapeRenderer.begin();
		console.debug(shapeRenderer);
		shapeRenderer.end();
		Gdx.gl.glDisable(GL20.GL_BLEND);
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
