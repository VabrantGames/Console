package com.vabrant.console;

import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

public class TextBox extends InputAdapter{

	private final char[] separators = {' ', '[', '(', ',', '.'};
	
	private float height;
	private String currentCommand = "";
	private final StringBuilder commandBuilder = new StringBuilder(50);
	private NinePatch patch;
	private final Console console;
	private final ConsoleFont font;
	
	public TextBox(Console console, TextureRegion region) {
		this.console = console;
		patch = new NinePatch(region, 1, 1, 1, 1);
		patch.setColor(Color.BLACK);
		height = console.bounds.height * 0.10f;
		font = new ConsoleFont();
		font.setFixedHeight(height);
		font.useMaxCharHeight();
	}
	
	public void clear() {
		commandBuilder.delete(0, commandBuilder.length());
	}

	public void draw(Batch batch) {
		patch.draw(batch, console.bounds.x, console.bounds.y, console.bounds.width, height);
		font.draw(batch);
	}
	
	public void debug(ShapeRenderer renderer) {
		renderer.set(ShapeType.Line);
		renderer.setColor(Color.GREEN);
		renderer.rect(console.bounds.x, console.bounds.y, console.bounds.width, height);
		
		font.debug(renderer);
	}
	
	@Override
	public boolean keyTyped(char character) {
		switch(character) {
			case 0:
				break;
			case 27: //esc
				break;
			case 127://del
				clear();
				break;
			case 8: //backspace
				if(commandBuilder.length() == 0) break;
				commandBuilder.deleteCharAt(commandBuilder.length() - 1);
				currentCommand = commandBuilder.toString();
				font.setText(currentCommand);
				break;
			case 13: //enter
//				parseCharsSplit();
//				methodString.delete(0, methodString.length());
//				clearText(font);
				break;
				
			case 'a': 
			case 'b': 
			case 'c': 
			case 'd': 
			case 'e': 
			case 'f': 
			case 'g': 
			case 'h': 
			case 'i': 
			case 'j': 
			case 'k': 
			case 'l': 
			case 'm': 
			case 'n': 
			case 'o': 
			case 'p': 
			case 'q': 
			case 'r': 
			case 's': 
			case 't': 
			case 'u': 
			case 'v': 
			case 'w': 
			case 'x': 
			case 'y': 
			case 'z': 
			case 'A': 
			case 'B': 
			case 'C': 
			case 'D': 
			case 'E': 
			case 'F': 
			case 'G': 
			case 'H': 
			case 'I': 
			case 'J': 
			case 'K': 
			case 'L': 
			case 'M': 
			case 'N': 
			case 'O': 
			case 'P': 
			case 'Q': 
			case 'R': 
			case 'S': 
			case 'T': 
			case 'U': 
			case 'V': 
			case 'W': 
			case 'X': 
			case 'Y': 
			case 'Z':
			case '0':
			case '1':
			case '2':
			case '3':
			case '4':
			case '5':
			case '6':
			case '7':
			case '8':
			case '9':
			case '(':
			case ')':
			case ',':
			case ' ':
			case '.':
			case '-':
			case '"':
			case '[':
			case ']':
			case '<':
			case '>':
				
				commandBuilder.append(character);
				currentCommand = commandBuilder.toString();
				font.setText(currentCommand);
				break;
			default:
//				System.err.println(notSupportedString);
				break;
	}
		return false;
	}
}
