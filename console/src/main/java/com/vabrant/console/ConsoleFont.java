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

public class ConsoleFont {
	
	private static int maxCharWidth;
	private static int maxCharHeight;
	private static final ObjectMap<Character, ImmutableGlyph> immutableGlyphs = new ObjectMap<>();
	
	public static void init(Texture fontTexture) {
		fontTexture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(Gdx.files.internal(Console.FONT_FNT_PATH).read()));
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
				
				if(width > maxCharWidth) maxCharWidth = width;
				if(height > maxCharHeight) maxCharHeight = height;
				
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
	private boolean centerHeight = true;
	private boolean useMaxCharHeight;
	private float fixedWidth;
	private float fixedHeight;
	public float x;
	public float y;
	private float width;
	private float height;
	private float scale = 0.12f;
	private Array<Glyph> text = new Array<>(20); 

	public void clear() {
		Pools.freeAll(text);
		text.clear();
	}
	
	private void resetText() {
		for(int i = 0; i < text.size; i++) {
			text.get(i).reset();
		}
	}
	
	public void setColor(Color color) {
		for(int i = 0; i < text.size; i++) {
			text.get(i).color.set(color);
		}
	}
	
	public void useMaxCharHeight() {
		useMaxCharHeight = true;
	}
	
	public float getHeight() {
		float height = useMaxCharHeight ? maxCharHeight : this.height;
		return text.size == 0 ? 0 : (height + 20) * scale;
	}
	
	public float getWidth() {
		return text.size == 0 ? 0 : (width * scale);
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
	}
	
	public void centerInsideFixedHeight(boolean center) {
		if(fixedHeight == 0) return;
		centerHeight = center;
	}
	
	public void setText(CharSequence chars, Color color) {
		clear();
		
		width = 0;
		height = 0;
		
		for(int i = 0; i < chars.length(); i++) {
			ImmutableGlyph immutableGlyph = immutableGlyphs.get(chars.charAt(i));
			if(immutableGlyph == null) continue;
			Glyph glyph = Pools.obtain(Glyph.class);
			glyph.set(immutableGlyph);
			glyph.color.set(color);
			text.add(glyph);
			
			width += (glyph.xadvance);
			if(glyph.height > height) height = glyph.height;
		}
	}
	
	public void draw(Batch batch) {
		float x = this.x;
		float y = this.y;
		
		if(centerWidth) x += (getFixedWidth() - getWidth()) / 2;
		if(centerHeight) y += (getFixedHeight() - getHeight()) / 2;
		
		for(int i = 0; i < text.size; i++) {
			Glyph glyph = text.get(i);
			if(glyph.region != null) {
				batch.setColor(glyph.color);
				batch.draw(glyph.region, x, y + (glyph.yOffset * scale) + (20 * scale), 0, 0, glyph.width * scale, glyph.height * scale, 1, 1, 0);
				batch.setColor(Color.WHITE);
			}
			x += (glyph.xadvance * scale);
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
	
		public int x;
		public int y;
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
