package com.vabrant.console.test;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
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
	
	@Override
	public void create() {
		assetManager = new AssetManager();
		console = new Console(assetManager, 0, 0, 300, 300);
		batch = new SpriteBatch();
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
	}

}
