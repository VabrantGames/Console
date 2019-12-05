package com.vabrant.console;

import java.util.regex.Pattern;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

import space.earlygrey.shapedrawer.ShapeDrawer;

public class TextBox extends InputAdapter{

	private final Pattern formatPattern;
	private float height;
	private final StringBuilder commandBuilder = new StringBuilder(50);
	private final Console console;
	private final ConsoleFont font;
	
	public TextBox(Console console) {
		this.console = console;
		height = console.bounds.height * 0.10f;
		font = new ConsoleFont();
		font.setFixedHeight(height);
		font.useMaxCharHeight();
		formatPattern = Pattern.compile(",\\s*|\\(\\s*|\\)|\\.\\s*|\\s+");
	}
	
	public void clear() {
		commandBuilder.delete(0, commandBuilder.length());
		font.clear();
	}

	public void draw(ShapeDrawer shapeDrawer, Batch batch) {
		shapeDrawer.filledRectangle(console.bounds.x, console.bounds.y, console.bounds.width, height, Color.BLACK);
		font.draw(batch);
	}
	
	public void debug(ShapeRenderer renderer) {
		renderer.set(ShapeType.Line);
		renderer.setColor(Color.GREEN);
		renderer.rect(console.bounds.x, console.bounds.y, console.bounds.width, height);
		
		font.debug(renderer);
	}
	
	private void parseInput() {
		String[] split = formatPattern.split(commandBuilder);
		for(String s : split) {
			System.out.println(s);
		}
		System.out.println("");
	}

	@Override
	public boolean keyDown(int keycode) {
		switch(keycode) {
			case Keys.BACKSPACE:
				if(commandBuilder.length() == 0) break;
				commandBuilder.deleteCharAt(commandBuilder.length() - 1);
				font.setText(commandBuilder, Color.WHITE);
				break;
			case Keys.ENTER:
				parseInput();
				clear();
				break;
		}
		return super.keyDown(keycode);
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
				
				break;
			case 13: //enter
				
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
//				currentCommand = commandBuilder.toString();
				font.setText(commandBuilder, Color.WHITE);
				break;
			default:
//				System.err.println(notSupportedString);
				break;
	}
		return false;
	}
}
