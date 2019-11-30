package com.vabrant.console;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;

public class Console {
	
	private final float scale = 1f;
	private AssetManager manager;
	final String DEBUG_ATLAS = "DebugAtlas.atlas";
	final Rectangle bounds;
	private final TextBox textBox;
	private ConsoleFont font;
	
	public Console(AssetManager assetManager, float x, float y, float width, float height) {
		assetManager.load(DEBUG_ATLAS, TextureAtlas.class);
		assetManager.load("ConsolasFont.png", Texture.class);
		assetManager.finishLoading();

		ConsoleFont.init(assetManager);
		font = new ConsoleFont();
		
		TextureAtlas atlas = assetManager.get(DEBUG_ATLAS);
		
		bounds = new Rectangle(x, y, width, height);
		textBox = new TextBox(this, atlas.findRegion("square"));
		Gdx.input.setInputProcessor(textBox);
	}
	
	public void draw(Batch batch) {
		textBox.draw(batch);
	}
	
	public void debug(ShapeRenderer renderer) {
		drawFrame(renderer);
		textBox.debug(renderer);
	}
	
	public void drawFrame(ShapeRenderer renderer) {
		renderer.set(ShapeType.Line);
		renderer.setColor(Color.GREEN);
		renderer.rect(bounds.x, bounds.y, bounds.width, bounds.height);
	}

}
