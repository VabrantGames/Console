package com.vabrant.console;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;

import space.earlygrey.shapedrawer.ShapeDrawer;

public class Console {
	
	public static final String FONT_TEXTURE_PATH = "ConsolasFont.png";
	public static final String FONT_FNT_PATH = "ConsolasFont.fnt";
	
	private final float scale = 1f;
	private AssetManager manager;
	final String DEBUG_ATLAS = "DebugAtlas.atlas";
	final Rectangle bounds;
	private final TextBox textBox;
	private ShapeDrawer shapeDrawer;
	
	public Console(Batch batch, TextureRegion pixel, AssetManager assetManager) {
		this(batch, pixel, assetManager.get(FONT_TEXTURE_PATH, Texture.class));
	}
	
	public Console(Batch batch, TextureRegion pixel, Texture fontTexture) {
		shapeDrawer = new ShapeDrawer(batch, pixel);
		ConsoleFont.init(fontTexture);
		bounds = new Rectangle(0, 0, 480, 300);
		textBox = new TextBox(this);
		Gdx.input.setInputProcessor(textBox);
	}
	
	public void draw(Batch batch) {
		textBox.draw(shapeDrawer, batch);
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
