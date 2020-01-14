package com.vabrant.console;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.badlogic.gdx.utils.Pools;
import com.badlogic.gdx.utils.reflect.ReflectionException;

import space.earlygrey.shapedrawer.ShapeDrawer;

public class TextBox extends InputAdapter{
	
	private DebugLogger logger = DebugLogger.getLogger(TextBox.class, DebugLogger.DEVELOPMENT_DEBUG);

	private static final Class[] emptyArgTypes = new Class[0];
	
	private final char[] separators = {' ', '.'};
	private final char BEGINNING_OF_LINE = '^';
	private final char END_OF_LINE = '$';

	private final float deleteDuration = 0.05f;
	private final float deleteDelayDuration = 0.3f;
	private boolean continuousDelete;
	private float deleteTimer;
	private float deleteDelayTimer;
	
	private boolean firstSection = true;
	private boolean invalidInput;
	private int cursorPosition;
	private float cursorX;
	private float cursorXOffset;
	private float textBoxXOffset;
	private float x;
	private float y;
	private float fontY;
	private float width;
	private float height;
	
	private final BitmapFont font;
	private final GlyphLayout layout;
	private final Pattern formatPattern;
	private final Matcher matcher;
	private final Console console;
	private final StringBuilder commandBuilder;
	private final IntArray separatorIndexes;
	private ExecutableObjectSection currentExecutableObjectSection;
	private Array<ExecutableObjectSection> executableObjectSections;
	private Array<CommandSection> sections;
	
	public TextBox(Console console) {
		this.console = console;
		
		executableObjectSections = new Array<>(10);
		sections = new Array<>(20);
		
		height = Console.HEIGHT * 0.10f;
		textBoxXOffset = Console.WIDTH * 0.03f;
		cursorXOffset = Console.WIDTH * 0.0045f;
		font = new BitmapFont(Gdx.files.internal(Console.FONT_FNT_PATH));
		font.getData().setScale(0.15f);
		fontY = font.getCapHeight() + Math.abs(font.getDescent());
		font.setColor(Color.GREEN);
		layout = new GlyphLayout();
		
//		formatPattern = Pattern.compile("\"[\\w\\s]*\"|^?\\W?\\w+\\W?\\$?");
		
		String numbersFormat = "\\d+\\.?\\d*[fFlLdD]?[$ ]?";
		
		formatPattern = Pattern.compile(numbersFormat + '|' + "[^ ]?\\w+[$ ]?");
		matcher = formatPattern.matcher("");
		commandBuilder = new StringBuilder(100);
		separatorIndexes = new IntArray(50);
	}
	
	private void poolAll(Array a) {
		for(int i = a.size - 1; i >= 0; i--) {
			Pools.free(a.pop());
		}
	}
	
	public void clear() {
		firstSection = true;
		invalidInput = false;
		currentExecutableObjectSection = null;
		separatorIndexes.clear();
		poolAll(sections);
		poolAll(executableObjectSections);
		commandBuilder.delete(0, commandBuilder.length());
		updateText(Color.WHITE);
		if(logger != null) logger.debug("Clear");
	}

	public void draw(Batch batch, ShapeDrawer shapeDrawer) {
		shapeDrawer.filledRectangle(x, y, console.bounds.width, height, Color.BLACK);
		
		float centerYToTextBoxSizeOffset = (height - font.getLineHeight()) / 2;
		
		font.draw(batch, layout, x + textBoxXOffset, fontY + centerYToTextBoxSizeOffset);
//		shapeDrawer.rectangle(x + textBoxXOffset, y + centerYToTextBoxSizeOffset, layout.width, font.getLineHeight(), Color.WHITE);
		shapeDrawer.filledRectangle(cursorX + textBoxXOffset, y + centerYToTextBoxSizeOffset, 2, font.getLineHeight(), Color.WHITE);
	}
	
	public void debug(ShapeDrawer shapeDrawer) {
		shapeDrawer.rectangle(console.bounds.x, console.bounds.y, console.bounds.width, height, Color.GREEN);
	}
	
	private void updateText(Color color) {
		font.setColor(color);
		layout.setText(font, commandBuilder);
		cursorX = cursorXOffset + layout.width;
	}
	
	private void deleteLastChar() {
		if(commandBuilder.length() == 0) return;
		int index = commandBuilder.length() - 1;
		commandBuilder.deleteCharAt(index);
		updateText(Color.WHITE);
		
//		System.out.println("commandStart: " + currentCommandSection.start);
		//remove any section that is out of scope (less than the index of the last char). 
//		if(index < currentCommandSection.start) {
//			if(logger != null) logger.devDebug("Remove Section : " + commandSections.size());
//			Pools.free(commandSections.remove(currentCommandSection));
//			currentCommandSection = commandSections.get(commandSections.size() - 1);
//		}
	}

	private Class getArgumentType(String argument) {
		if(argument.isEmpty()) return null;
		
		Class c = boolean.class;
		
		char firstChar = argument.charAt(0);
		
		if(Character.isDigit(firstChar)) {
			if(argument.length() == 1) {
				c = int.class;
			}
			else if(argument.contains(".")) {
				switch(argument.charAt(argument.length() - 1)) {
					case 'f':
					case 'F':
						c = float.class;
						break;
					case 'd':
					case 'D':
						c = double.class;
						break;
					default:
						c = float.class;
						break;
				}
			}
			else {
				switch(argument.charAt(argument.length() - 1)) {
					case 'f':
					case 'F':
						c = float.class;
						break;
					case 'l':
					case 'L':
						c = long.class;
						break;
					case 'd':
					case 'D':
						c = double.class;
						break;
					default:
						c = int.class;
						break;
				}
			}
		}
		else if(Character.isLetter(firstChar)) {
			c = Object.class;
		}
		
		return c;
	}
	
	private boolean isSeparator(char c) {
		for(int i = 0; i < separators.length; i++) {
			if(c == separators[i]) return true;
		}
		return false;
	}
	
	public ExecutableObjectSection createExecutableObjectSection(String s, char leftSeparator, char rightSeparator) {
		//first section
		if(firstSection) {
			
		}
		
		return null;
	}
	
	private void parseInput() throws Exception{
//		matcher.reset(commandBuilder);
		
		int start = 0;
		final int maxIndex = commandBuilder.length() - 1;
		char separator = BEGINNING_OF_LINE;
		char lastSeparator;
		
		for(int i = 0, length = separatorIndexes.size + 1; i < length; i++) {
			lastSeparator = separator;
			int end = 0;

			if(i > length - 2) {
				separator = END_OF_LINE;
				end = maxIndex + 1;
			}
			else {
				int sepIndex = separatorIndexes.get(i);
				separator = commandBuilder.charAt(sepIndex);
				end = sepIndex;
			}

			String section = commandBuilder.substring(start, end);
			start = end + 1;

			//create the root executable object
			if(firstSection) {
				firstSection = false;
				currentExecutableObjectSection = Pools.obtain(ExecutableObjectSection.class);
				executableObjectSections.add(currentExecutableObjectSection);

				currentExecutableObjectSection.commandObjectSection = Pools.obtain(CommandObjectSection.class);
				boolean valid = currentExecutableObjectSection.commandObjectSection.check(console, section);
				if(!valid) {
					if(logger != null) logger.error("No such object");
					break;
				}
				currentExecutableObjectSection.hasObject = true;
			}
			else {
				if(!currentExecutableObjectSection.hasMethod) {
					if(lastSeparator == '.' && separator == '.') throw new RuntimeException("Invalid Format");

					currentExecutableObjectSection.hasMethod = true;

					boolean valid = currentExecutableObjectSection.commandObjectSection.commandObject.containsMethod(section);
					if(!valid) {
						if(logger != null) logger.error("No such method");
						return;
					}
					currentExecutableObjectSection.commandMethodSection = Pools.obtain(CommandMethodSection.class);
					currentExecutableObjectSection.commandMethodSection.name = section;
				}
				else {
					Class argType = getArgumentType(section);

					if(argType.equals(int.class)) {
						IntArgument sec = Pools.obtain(IntArgument.class);
						sec.value = Integer.parseInt(section);
						currentExecutableObjectSection.argumentSections.add(sec);
					}
					else if(argType.equals(float.class)) {
						FloatArgument sec = Pools.obtain(FloatArgument.class);
						sec.value = Float.parseFloat(section);
						currentExecutableObjectSection.argumentSections.add(sec);
					}
					else if(argType.equals(double.class)) {
						DoubleArgument sec = Pools.obtain(DoubleArgument.class);
						sec.value = Double.parseDouble(section);
						currentExecutableObjectSection.argumentSections.add(sec);
					}
					else if(argType.equals(long.class)) {
						char c = section.charAt(section.length() - 1);

						if(c == 'l' || c == 'L') {
							section = section.substring(0, section.length() - 1);
							argType = getArgumentType(section);
						}

						LongArgument sec = Pools.obtain(LongArgument.class);
						sec.value = Long.parseLong(section);
						currentExecutableObjectSection.argumentSections.add(sec);
					}
					else if(argType.equals(Object.class)) {
						ObjectArgument sec = Pools.obtain(ObjectArgument.class);
						CommandObject object = console.getCommandObject(section);

						//do something
						if(object == null) {
						}

						sec.commandObject = object;
						currentExecutableObjectSection.argumentSections.add(sec);
					}
				}
			}
		}
		
		executableObjectSections.get(0).execute();
//		try {
//		}
//		catch(RuntimeException | ReflectionException e) {
//			e.printStackTrace();
//		}

//		char separator;
//		while(matcher.find()) {
//			String s = matcher.group();
//			
//			char lastChar = s.charAt(s.length() - 1);
//			
//			if(!isSeparator(lastChar)) {
//				separator = END_OF_LINE;
//			}
//			else {
//				s = s.substring(0, s.length() - 1);
//			}
//			
//			System.out.println(s);
//			
//			if(firstSection) {
//				firstSection = false;
//				mainObjectSection = Pools.obtain(ExecutableObjectSection.class);
//				mainObjectSection.commandObjectSection = Pools.obtain(CommandObjectSection.class);
//				
//				boolean valid = mainObjectSection.commandObjectSection.check(console, s);
//				if(!valid) {
//					if(logger != null) logger.error("No such object");
//					break;
//				}
//				mainObjectSection.hasObject = true;
//			}
//			else {
//				if(!mainObjectSection.hasMethod) {
//					mainObjectSection.hasMethod = true;
//					
//					mainObjectSection.commandMethodSection = Pools.obtain(CommandMethodSection.class);
//					
//					boolean valid = mainObjectSection.commandObjectSection.commandObject.containsMethod(s);
//					if(!valid) {
//						if(logger != null) logger.error("No such method");
//						break;
//					}
//					mainObjectSection.commandMethodSection.name = s;
//				}
//				else {
//					//arguments
//					Class argType = getArgumentType(s);
//					
//					if(argType.equals(int.class)) {
//						IntArgument sec = Pools.obtain(IntArgument.class);
//						sec.value = Integer.parseInt(s);
//						mainObjectSection.argumentSections.add(sec);
//					}
//					else if(argType.equals(float.class)) {
//						FloatArgument sec = Pools.obtain(FloatArgument.class);
//						sec.value = Float.parseFloat(s);
//						mainObjectSection.argumentSections.add(sec);
//					}
//					else if(argType.equals(double.class)) {
//						DoubleArgument sec = Pools.obtain(DoubleArgument.class);
//						sec.value = Double.parseDouble(s);
//						mainObjectSection.argumentSections.add(sec);
//					}
//					else if(argType.equals(long.class)) {
//						char c = s.charAt(s.length() - 1);
//						
//						if(c == 'l' || c == 'L') {
//							s = s.substring(0, s.length() - 1);
//							argType = getArgumentType(s);
//						}
//						
//						LongArgument sec = Pools.obtain(LongArgument.class);
//						sec.value = Long.parseLong(s);
//						mainObjectSection.argumentSections.add(sec);
//					}
//					else if(argType.equals(Object.class)) {
//						ObjectArgument sec = Pools.obtain(ObjectArgument.class);
//						CommandObject object = console.getCommandObject(s);
//
//						//do something
//						if(object == null) {
//						}
//						
//						sec.commandObject = object;
//						mainObjectSection.argumentSections.add(sec);
//					}
//				}
//			}
//		}
//		
//		try {
//			mainObjectSection.execute();
//		}
//		catch(RuntimeException | ReflectionException e) {
//			e.printStackTrace();
//		}
	}

	public void update(float delta) {
		if(continuousDelete) {
			deleteTimer += delta;
			if((deleteDelayTimer += delta) >= deleteDelayDuration) {
				if((deleteTimer += delta) > deleteDuration) {
					deleteTimer = 0;
					deleteLastChar();
				}
			}
		}
	}
	
	@Override
	public boolean keyDown(int keycode) {
		switch(keycode) {
			case Keys.BACKSPACE:
				continuousDelete = true;
				deleteLastChar();
				break;
			case Keys.ENTER:
				try {
					parseInput();
				}
				catch(Exception e) {
					e.printStackTrace();
				}
				clear();
				break;
		}
		return super.keyDown(keycode);
	}
	
	@Override
	public boolean keyUp(int keycode) {
		switch(keycode) {
			case Keys.BACKSPACE:
				deleteTimer = 0;
				deleteDelayTimer = 0;
				continuousDelete = false;
				break;
		}
		return super.keyUp(keycode);
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
//			case '<':
//			case '>':
//			case '+':
				
				//restrict multiple spaces 
				int length = commandBuilder.length();
				if(length > 0 && commandBuilder.charAt(length - 1) == ' ' && character == ' ') {
					System.out.println("Multiple spaces not allowed");
					return false;
				}
				
				commandBuilder.append(character);
				updateText(Color.WHITE);
				
				if(isSeparator(character)) {
					separatorIndexes.add(commandBuilder.length() - 1);
//					Separator separator = getSeparator(character);
//					checkSection(currentCommandSection);
//					nextCommandSection();
				}
				
//				updateState();
				break;
			default:
//				System.err.println(notSupportedString);
				break;
		}
		return false;
	}
	
	private static class CommandSection implements Poolable{
		int start;
		int end;
		
		@Override
		public void reset() {
			start = 0;
			end = 0;
		}
	}

	private interface Argument{
		public boolean isValid();
		public Class getArgumentType();
		public Object getArgument();
	}
	
	private static class ExecutableObjectSection extends CommandSection implements Argument {
		
		boolean hasObject;
		boolean hasMethod;
		CommandObjectSection commandObjectSection;
		CommandMethodSection commandMethodSection;
		Array<Argument> argumentSections = new Array<>(3);
		Class returnType;
		Object argument;
		
		public void execute() throws ReflectionException, RuntimeException{
			Class[] argTypes = emptyArgTypes;
			Object[] args = null;
			
			if(argumentSections.size > 0) {
				argTypes =  new Class[argumentSections.size];
				
				for(int i = 0; i < argumentSections.size; i++) {
					argTypes[i] = argumentSections.get(i).getArgumentType();
				}
			}
			
			CommandMethod method = commandObjectSection.commandObject.getMethod(commandMethodSection.name, argTypes);
			
			if(method == null) throw new RuntimeException("Method does not exist.");
			
			returnType = method.getReturnType();
			
			if(argTypes != null) {
				args = new Object[argumentSections.size];
				
				for(int i = 0; i < argumentSections.size; i++) {
					Argument section = argumentSections.get(i);
					args[i] = section.getArgument();
				}
			}
			
			argument =  method.invoke(commandObjectSection.getObject(), args);
		}
		
		@Override
		public boolean isValid() {
			return true;
		}
		
		@Override
		public Class getArgumentType() {
			return returnType;
		}
		
		@Override
		public Object getArgument() {
			return argument;
		}
		
		@Override
		public void reset() {
			super.reset();
			hasObject = false;
			hasMethod = false;
			returnType = null;
			argument = null;
			if(commandObjectSection != null) Pools.free(commandObjectSection);
			if(commandMethodSection != null) Pools.free(commandMethodSection);
			commandObjectSection = null;
			commandMethodSection = null;
			for(int i = argumentSections.size - 1; i >= 0; i--) {
				Pools.free(argumentSections.pop());
			}
			System.out.println("Pool");
		}
	}
	
	private static class ObjectArgument extends CommandSection implements Argument{

		CommandObject commandObject;
		
		@Override
		public boolean isValid() {
			return true;
		}
		
		@Override
		public Class getArgumentType() {
			return commandObject == null ? null : commandObject.getObject().getClass();
		}

		@Override
		public Object getArgument() {
			return commandObject.getObject();
		}
		
		@Override
		public void reset() {
			super.reset();
			commandObject = null;
		}
		
	}
	
	private static class IntArgument extends CommandSection implements Argument {
		
		int value = 0;
		
		@Override
		public boolean isValid() {
			return true;
		}
		
		@Override
		public Class getArgumentType() {
			return int.class;
		}
		
		@Override
		public Object getArgument() {
			return value;
		}
		
		@Override
		public void reset() {
			super.reset();
			value = 0;
		}
	}
	
	private static class LongArgument extends CommandSection implements Argument {
		
		long value = 0;
		
		@Override
		public boolean isValid() {
			return true;
		}
		
		@Override
		public Class getArgumentType() {
			return long.class;
		}
		
		@Override
		public Object getArgument() {
			return value;
		}
		
		@Override
		public void reset() {
			super.reset();
			value = 0;
		}
	}
	
	private static class FloatArgument extends CommandSection implements Argument {
		
		float value = 0;
		
		@Override
		public boolean isValid() {
			return true;
		}

		@Override
		public Class getArgumentType() {
			return float.class;
		}

		@Override
		public Object getArgument() {
			return value;
		}
		
		@Override
		public void reset() {
			super.reset();
			value = 0;
		}
	}
	
	private static class DoubleArgument extends CommandSection implements Argument {
		
		double value = 0;
		
		@Override
		public boolean isValid() {
			return true;
		}
		
		@Override
		public Class getArgumentType() {
			return double.class;
		}
		
		@Override
		public Object getArgument() {
			return value;
		}
		
		@Override
		public void reset() {
			super.reset();
			value = 0;
		}
	}
	
	private static class CommandObjectSection extends CommandSection { 
		
		private CommandObject commandObject;
		
		public boolean check(Console console, String name) {
			CommandObject o = console.getCommandObject(name);
			if(o != null) {
				commandObject = o;
				return true;
			}
			return false;
		}
		
		public CommandObject getCommandObject() {
			return commandObject;
		}
		
		public Object getObject() {
			return commandObject.getObject();
		}
		
		@Override
		public void reset() {
			super.reset();
			commandObject = null;
		}
	}
	
	private static class CommandMethodSection extends CommandSection {

		String name;
		CommandMethod commandMethod;
		
		public boolean check(CommandObject commandObject, String name) {
			if(commandObject.containsMethod(name)) {
				this.name = name;
				return true;
			}
			return false;
		}
		
		public void set(CommandObject commandObject) {
			
		}

		public CommandMethod getCommandMethod() {
			return commandMethod;
		}
		
		@Override
		public void reset() {
			super.reset();
			name = null;
			commandMethod = null;
		}
	}
	
}
