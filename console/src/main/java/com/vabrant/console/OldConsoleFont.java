package com.vabrant.console;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.badlogic.gdx.utils.Pools;
import com.badlogic.gdx.utils.StreamUtils;

public class OldConsoleFont {
	
	private static int MAX_CHARACTER_WIDTH;
	private static int MAX_CHARACTER_HEIGHT;
	private static final ObjectMap<Character, ImmutableGlyph> immutableGlyphs = new ObjectMap<>();
	
	public static void init(Texture fontTexture) {
		fontTexture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		
		BufferedReader reader = null;
		try {
//			reader = new BufferedReader(new InputStreamReader(Gdx.files.internal(Console.FONT_FNT_PATH).read()));
			reader.readLine();
			reader.readLine();
			reader.readLine();
			reader.readLine();
			String line = reader.readLine();
			
			while(true) {
				if(!line.startsWith("char ")) break;
				
				StringTokenizer tokens = new StringTokenizer(line, " =");
				tokens.nextToken();//char

				tokens.nextToken();//id
				int id = Integer.parseInt(tokens.nextToken());
				tokens.nextToken();//x
				int x = Integer.parseInt(tokens.nextToken());
				tokens.nextToken();//y
				int y = Integer.parseInt(tokens.nextToken());
				tokens.nextToken();//width
				int width = Integer.parseInt(tokens.nextToken());
				tokens.nextToken();//height
				int height = Integer.parseInt(tokens.nextToken());
				
				if(width > MAX_CHARACTER_WIDTH) MAX_CHARACTER_WIDTH = width;
				if(height > MAX_CHARACTER_HEIGHT) MAX_CHARACTER_HEIGHT = height;
				
				tokens.nextToken();//xOffset
				tokens.nextToken();
				tokens.nextToken();//yOffset
				tokens.nextToken();
				
				tokens.nextToken();//xadvance
				int xadvance = Integer.parseInt(tokens.nextToken());

				int yOffset = 0;

				switch((char)id) {
					case 'j':
					case 'p':
					case 'q':
					case 'y':
					case 'g':
					case 'Q':
					case '(':
					case ')':
					case ',':
					case '[':
					case ']':
						yOffset = -20;
						break;
					case '"':
						yOffset = 50;
						break;
				}
				
				ImmutableGlyph g = new ImmutableGlyph(width, height, 0, yOffset, xadvance, new TextureRegion(fontTexture, x, y, width, height));
				immutableGlyphs.put((char)id, g);
				
				line = reader.readLine();
			}
		}
		catch(Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Can't read font file");
		}
		finally {
			StreamUtils.closeQuietly(reader);
		}
	}
	
	private boolean centerWidth;
	private boolean centerHeight;
	private float fixedWidth;
	private float fixedHeight;
	private float x;
	private float y;
	private float drawingX;
	private float drawingY;
	private float width;
	private float height;
	private float scale = 0.12f;
	private Array<Glyph> glyphs = new Array<>(20); 
	private StringBuilder textBuilder = new StringBuilder();
	
	public StringBuilder getStringBuilder() {
		return textBuilder;
	}
	
	public int length() {
		return textBuilder.length();
	}

	public void clear() {
		Pools.freeAll(glyphs);
		glyphs.clear();
		textBuilder.delete(0, textBuilder.length());
	}
	
	private void resetText() {
		for(int i = 0; i < glyphs.size; i++) {
			glyphs.get(i).reset();
		}
	}
	
	public void setColor(Color color) {
		for(int i = 0; i < glyphs.size; i++) {
			glyphs.get(i).color.set(color);
		}
	}
	
	public float getX() {
		return x;
	}
	
	public float getDrawingX() {
		return drawingX;
	}
	
	public float getY() {
		return y;
	}
	
	public float getDrawingY() {
		return drawingY;
	}
	
	public void setX(float x) {
		this.x = x;
		repositionGlyphs();
	}
	
	public void setY(float y) {
		repositionGlyphs();
	}
	
	public void setPosition(float x, float y) {
		this.x = x;
		this.y = y;
		repositionGlyphs();
	}
	public float getHeight() {
		return glyphs.size == 0 ? 0 : (height + 20) * scale;
	}
	
	public float getWidth() {
		return glyphs.size == 0 ? 0 : (width * scale);
	}
	
	public float getFixedWidth() {
		return fixedWidth;
	}
	
	public float getFixedHeight() {
		return fixedHeight;
	}
	
	public void setFixedHeight(float size) {
		fixedHeight = size;
	}
	
	public void setFixedWidth(float size) {
		fixedWidth = size;
	}
	
	public void centerInsideFixedWidth(boolean center) {
		if(fixedWidth == 0) return;
		centerWidth = center;
		repositionGlyphs();
	}
	
	public void centerInsideFixedHeight(boolean center) {
		if(fixedHeight == 0) return;
		centerHeight = center;
		repositionGlyphs();
	}
	
	public void remove(int index) {
		Glyph glyph = glyphs.removeIndex(index);
		
		width -= (glyph.xadvance);
		
		//reset the height
		height = 0;
		for(int i = 0; i < glyphs.size; i++) {
			Glyph g = glyphs.get(i);
			if(g.height > height) height = g.height;
		}
		
		Pools.free(glyph);
		textBuilder.deleteCharAt(index);
	}
	
	public void appendChar(char c, Color color) {
		ImmutableGlyph immutableGlyph = immutableGlyphs.get(c);
		if(immutableGlyph == null) return;
		textBuilder.append(c);
		Glyph glyph = Pools.obtain(Glyph.class);
		glyph.set(immutableGlyph);
		glyph.color.set(color);
		glyphs.add(glyph);
		
		width += glyph.xadvance;
		if(glyph.height > height) height = glyph.height;
		repositionGlyphs();
	}
	
	public void setText(CharSequence chars, Color color) {
		clear();
		
		width = 0;
		height = 0;
		
		for(int i = 0; i < chars.length(); i++) {
			ImmutableGlyph immutableGlyph = immutableGlyphs.get(chars.charAt(i));
			if(immutableGlyph == null) continue;
			textBuilder.append(chars.charAt(i));
			Glyph glyph = Pools.obtain(Glyph.class);
			glyph.set(immutableGlyph);
			glyph.color.set(color);
			glyphs.add(glyph);
			
			width += (glyph.xadvance);
			if(glyph.height > height) height = glyph.height;
		}
		
		repositionGlyphs();
	}
	
	private void repositionGlyphs() {
		float x = this.x;
		float y = this.y;

		if(centerWidth) x += (getFixedWidth() - getWidth()) / 2;
		if(centerHeight) y += (getFixedHeight() - getHeight()) / 2;
		
		drawingX = x;
		drawingY = y;
		
		for(int i = 0; i < glyphs.size; i++) {
			Glyph glyph = glyphs.get(i);
			if(glyph.region != null) {
				glyph.x = x;
				glyph.y = y + (glyph.yOffset * scale) + (20 * scale);
			}
			x += (glyph.xadvance * scale);
		}
	}
	
	public void draw(Batch batch) {
		for(int i = 0; i < glyphs.size; i++) {
			Glyph glyph = glyphs.get(i);
			if(glyph.region != null) {
				batch.setColor(glyph.color);
				batch.draw(glyph.region, glyph.x, glyph.y, 0, 0, glyph.width, glyph.height, scale, scale, 0);
				batch.setColor(Color.WHITE);
			}
		}
	}
	
	public void debug(ShapeRenderer renderer) {
		renderer.set(ShapeType.Line);
		renderer.setColor(Color.RED);

		if(fixedWidth != 0 || fixedHeight != 0) {
			renderer.rect(x, y, fixedWidth == 0 ? getWidth() : getFixedWidth(), fixedHeight == 0 ? getHeight() : getFixedHeight());
		}
		
		renderer.setColor(Color.GREEN);
		
		float x = this.x;
		float y = this.y;
		
		if(centerWidth) x += (getFixedWidth() - getWidth()) / 2;
		if(centerHeight) y += (getFixedHeight() - getHeight()) / 2;
		
		renderer.rect(x, y, getWidth(), getHeight());
	}

	private static class ImmutableGlyph {
		final int width;
		final int height;
		final int xadvance;
		final int xOffset;
		final int yOffset;
		final TextureRegion region;
		
		public ImmutableGlyph(int width, int height, int xOffset, int yOffset, int xadvance, TextureRegion region) {
			this.width = width;
			this.height = height;
			this.xOffset = xOffset;
			this.yOffset = yOffset;
			this.xadvance = xadvance;
			this.region = region;
		}
	}
 
	public static class Glyph implements Poolable{
	
		public float x;
		public float y;
		public int width;
		public int height;
		public int xOffset;
		public int yOffset;
		public int xadvance;
		private TextureRegion region;
		private Color color = new Color(Color.WHITE);
		
		public void set(ImmutableGlyph immutableGlyph) {
			width = immutableGlyph.width;
			height = immutableGlyph.height;
			yOffset = immutableGlyph.yOffset;
			xadvance = immutableGlyph.xadvance;
			region = immutableGlyph.region;
		}
		
		@Override
		public void reset() {
			x = 0;
			y = 0;
			width = 0;
			height = 0;
			xadvance = 0;
			region = null;
			color.set(Color.WHITE);
		}
		
	}
}
