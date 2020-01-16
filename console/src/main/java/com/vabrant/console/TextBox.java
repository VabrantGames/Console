package com.vabrant.console;

import java.util.Iterator;
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
import com.badlogic.gdx.utils.ObjectSet;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.badlogic.gdx.utils.Pools;

import space.earlygrey.shapedrawer.ShapeDrawer;

public class TextBox extends InputAdapter {

	private DebugLogger logger = DebugLogger.getLogger(TextBox.class, DebugLogger.DEVELOPMENT_DEBUG);

	private static final Class[] EMPTY_ARG_TYPES = new Class[0];
	private static final char[] separators = {' '};
	private static final char BEGINNING_OF_LINE_SEPARATOR = '^';
	private static final char END_OF_LINE_SEPARATOR = '$';
	private static final char EMPTY_SEPARATOR = '!';

	private final float deleteDuration = 0.05f;
	private final float deleteDelayDuration = 0.3f;
	private boolean continuousDelete;
	private float deleteTimer;
	private float deleteDelayTimer;

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
	private final Console console;
	private final StringBuilder commandBuilder;
	private final Array<SeparatorSection> separatorSections;
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

		commandBuilder = new StringBuilder(100);
		separatorSections = new Array<>(10);

		//set the beginning section
		SeparatorSection beginningSeparator = Pools.obtain(SeparatorSection.class);
		beginningSeparator.separator = BEGINNING_OF_LINE_SEPARATOR;
		beginningSeparator.setIndexes(-1, -1);
		separatorSections.add(beginningSeparator);
	}

	private void poolAll(Array a, int start, int end) {
		for (int i = end; i >= start; i--) {
			Pools.free(a.pop());
		}
	}

	public void clear() {
		cursorPosition = 0;
		invalidInput = false;
		currentExecutableObjectSection = null;
		poolAll(separatorSections, 1, separatorSections.size - 1);
		poolAll(sections, 0, sections.size - 1);
		poolAll(executableObjectSections, 0, executableObjectSections.size - 1);
		commandBuilder.delete(0, commandBuilder.length());
		updateText(Color.WHITE);
		if (logger != null) logger.debug("Clear");
	}

	public void draw(Batch batch, ShapeDrawer shapeDrawer) {
		shapeDrawer.filledRectangle(x, y, console.bounds.width, height, Color.BLACK);

		float centerYToTextBoxSizeOffset = (height - font.getLineHeight()) / 2;
		font.draw(batch, layout, x + textBoxXOffset, fontY + centerYToTextBoxSizeOffset);
		
		shapeDrawer.filledRectangle(cursorX + textBoxXOffset, y + centerYToTextBoxSizeOffset, 2, font.getLineHeight(), Color.WHITE);
	}

	public void debug(ShapeDrawer shapeDrawer) {
		//text width & height debug
//		shapeDrawer.rectangle(x + textBoxXOffset, y + centerYToTextBoxSizeOffset, layout.width, font.getLineHeight(), Color.GREEN);
//		shapeDrawer.rectangle(console.bounds.x, console.bounds.y, console.bounds.width, height, Color.GREEN);
	}

	private void updateText(Color color) {
		font.setColor(color);
		layout.setText(font, commandBuilder);
		cursorX = cursorXOffset + layout.width;
	}

	private void deleteLastChar() {
		if (commandBuilder.length() == 0) return;
		int index = commandBuilder.length() - 1;
		commandBuilder.deleteCharAt(index);
		updateText(Color.WHITE);

//		System.out.println("commandStart: " + currentCommandSection.start);
		// remove any section that is out of scope (less than the index of the last
		// char).
//		if(index < currentCommandSection.start) {
//			if(logger != null) logger.devDebug("Remove Section : " + commandSections.size());
//			Pools.free(commandSections.remove(currentCommandSection));
//			currentCommandSection = commandSections.get(commandSections.size() - 1);
//		}
	}
	
	private boolean isSeparator(char c) {
		for (int i = 0; i < separators.length; i++) {
			if (c == separators[i]) return true;
		}
		return false;
	}

	public void checkSection(String s, SeparatorSection leftSeparator, SeparatorSection rightSeparator) throws Exception {
		char firstChar = s.charAt(0);
		
		//an object
		if (Character.isLetter(firstChar)) {
			
			//an executable object
			if (s.contains(".")) {
				
				
//				String[] objectAndMethod = s.split(".");
//				
//				if(objectAndMethod.length > 2) throw new RuntimeException("Invalid format");
//				
				
			}
			else {
				if(!currentExecutableObjectSection.isValid()) {
					if(!currentExecutableObjectSection.hasObject()) {
						currentExecutableObjectSection.checkObject(console, s);
					}
					else {
						currentExecutableObjectSection.checkIfMethodWithNameExists(console, s);
					}
				}
			}
		}
		else if (Character.isDigit(firstChar)) {
			if(s.length() == 1) {
				IntArgument arg = Pools.obtain(IntArgument.class);
				arg.value = Integer.parseInt(s);
				currentExecutableObjectSection.addArgument(arg);
			}
			else if(s.contains(".")){
				switch(s.charAt(s.length() - 1)) {
					case 'd':
					case 'D':
						DoubleArgument dArg = Pools.obtain(DoubleArgument.class);
						dArg.value = Double.parseDouble(s);
						currentExecutableObjectSection.addArgument(dArg);
						break;
					default:
						FloatArgument fArg = Pools.obtain(FloatArgument.class);
						fArg.value = Float.parseFloat(s);
						currentExecutableObjectSection.addArgument(fArg);
						break;
				}
			}
			else {
				char lastChar = s.charAt(s.length() - 1);
				switch (lastChar) {
					case 'f':
					case 'F':
						FloatArgument fArg = Pools.obtain(FloatArgument.class);
						fArg.value = Float.parseFloat(s);
						currentExecutableObjectSection.addArgument(fArg);
						break;
					case 'l':
					case 'L':
						LongArgument lArg = Pools.obtain(LongArgument.class);
						lArg.value = Long.parseLong(s.substring(0, s.length() - 1));
						currentExecutableObjectSection.addArgument(lArg);
						break;
					case 'd':
					case 'D':
						DoubleArgument dArg = Pools.obtain(DoubleArgument.class);
						dArg.value = Double.parseDouble(s);
						currentExecutableObjectSection.addArgument(dArg);
						break;
					default:
						IntArgument arg = Pools.obtain(IntArgument.class);
						arg.value = Integer.parseInt(s);
						currentExecutableObjectSection.addArgument(arg);
						break;
				}
			}
		}
		else if (firstChar == '.') {
			if(s.length() < 2) throw new RuntimeException("Section input is too short");
			
			s = s.substring(1, s.length());
			
			if(Character.isLetter(s.charAt(0))) {
				//allows the first object to be called with the method specifier '.'
				if(!currentExecutableObjectSection.isValid()) {
					currentExecutableObjectSection.checkIfMethodWithNameExists(console, s);
				}
				else {
					ExecutableObjectSection sec = Pools.obtain(ExecutableObjectSection.class);
					sec.checkIfMethodWithNameExists(console, s);
					currentExecutableObjectSection.addArgument(sec);
				}
			}
			else {
				switch(s.charAt(s.length() - 1)) {
					case 'd':
					case 'D':
						DoubleArgument dArg = Pools.obtain(DoubleArgument.class);
						dArg.value = Double.parseDouble(s);
						currentExecutableObjectSection.addArgument(dArg);
						break;
					default:
						FloatArgument fArg = Pools.obtain(FloatArgument.class);
						fArg.value = Float.parseFloat(s);
						currentExecutableObjectSection.addArgument(fArg);
						break;
				}
			}
		}
	}

	private void parseInput() throws Exception {
		SeparatorSection endSeparatorSection = Pools.obtain(SeparatorSection.class);
		endSeparatorSection.separator = END_OF_LINE_SEPARATOR;
		endSeparatorSection.setIndexes(commandBuilder.length(), commandBuilder.length());
		separatorSections.add(endSeparatorSection);
		
		//create the first section
		currentExecutableObjectSection = Pools.obtain(ExecutableObjectSection.class);
		executableObjectSections.add(currentExecutableObjectSection);

		for (int i = 0, length = separatorSections.size - 1; i < length; i++) {
			SeparatorSection leftSeparator = separatorSections.get(i);
			SeparatorSection rightSeparator = separatorSections.get(i + 1);

			int start = leftSeparator.getEndIndex() + 1;
			int end = rightSeparator.getEndIndex();

			String s = commandBuilder.substring(start, end);

			checkSection(s, leftSeparator, endSeparatorSection);
		}
		
		executableObjectSections.get(0).execute();
	}

	public void update(float delta) {
		if (continuousDelete) {
			deleteTimer += delta;
			if ((deleteDelayTimer += delta) >= deleteDelayDuration) {
				if ((deleteTimer += delta) > deleteDuration) {
					deleteTimer = 0;
					deleteLastChar();
				}
			}
		}
	}

	@Override
	public boolean keyDown(int keycode) {
		switch (keycode) {
			case Keys.BACKSPACE:
				continuousDelete = true;
				deleteLastChar();
				break;
			case Keys.ENTER:
				try {
					parseInput();
				}
				catch (Exception e) {
					e.printStackTrace();
				}
				clear();
				break;
		}
		return super.keyDown(keycode);
	}

	@Override
	public boolean keyUp(int keycode) {
		switch (keycode) {
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
		switch (character) {
			case 0:
				break;
			case 27: // esc
				break;
			case 127:// del
				clear();
				break;
			case 13: // enter

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

				// restrict multiple spaces
				int length = commandBuilder.length();
				if (length > 0 && commandBuilder.charAt(length - 1) == ' ' && character == ' ') {
					System.out.println("Multiple spaces not allowed");
					return false;
				}

				commandBuilder.append(character);
				updateText(Color.WHITE);

				if (isSeparator(character)) {
					int index = commandBuilder.length() - 1;
					SeparatorSection section = Pools.obtain(SeparatorSection.class);
					section.separator = character;
					section.isValid = true;
					section.setIndexes(index, index);
					separatorSections.add(section);
				}

				break;
			default:
				break;
		}
		return false;
	}

	private static class CommandSection implements Poolable {
		private int start;
		private int end;
		boolean isValid;

		public void setIndexes(int start, int end) {
			this.start = start;
			this.end = end;
		}

		public int getStartIndex() {
			return start;
		}

		public int getEndIndex() {
			return end;
		}

		public boolean isValid() {
			return isValid;
		}

		@Override
		public void reset() {
			start = 0;
			end = 0;
			isValid = false;
		}
	}

	private static class SeparatorSection extends CommandSection {
		char separator = TextBox.EMPTY_SEPARATOR;

		@Override
		public void reset() {
			super.reset();
			separator = TextBox.EMPTY_SEPARATOR;
		}
	}

	private interface Argument {
		public Class getArgumentType();
		public Object getArgument();
		public void execute() throws Exception;
	}

	private static class ExecutableObjectSection extends CommandSection implements Argument {

		private boolean alreadyExecuted;
		private boolean findCommandObjectDuringExecution;
		private boolean hasObject;
		private boolean hasMethod;
		private String methodName;
		private CommandObject commandObject;
		private ObjectSet<CommandObject> objectsWithMethod;
		private final Array<Argument> argumentSections = new Array<>(3);
		private Class returnType;
		private Object argument;

		public boolean hasObject() {
			return hasObject;
		}

		public boolean hasMethod() {
			return hasMethod;
		}
		
		public void addArgument(Argument arg) {
			argumentSections.add(arg);
		}

		public void setCommandObject(CommandObject object) throws IllegalArgumentException {
			if (object == null) throw new IllegalArgumentException("Object is null");
			commandObject = object;
		}

		public void checkObject(Console console, String s) throws RuntimeException {
			CommandObject object = console.getCommandObject(s);
			if (object == null) throw new RuntimeException("No such Object");
			commandObject = object;
			hasObject = true;
		}

		public void checkIfMethodWithNameExists(Console console, String s) throws RuntimeException {
			if (commandObject != null) {
				if (!commandObject.containsMethod(s)) throw new RuntimeException("Object " + commandObject.getName() + " doesn't contain a method called " + s);
			}
			else {
				if (!console.hasMethod(s)) throw new RuntimeException("Console doesn't contain a method called " + s);
				objectsWithMethod = console.getCommandObjectsThatHaveMethod(s);
				findCommandObjectDuringExecution = true;
			}
			methodName = s;
			hasMethod = true;
		}

		public void check(Console console, String s) {

		}

		@Override
		public void execute() throws Exception {
			if (!isValid()) throw new RuntimeException("Command is invalid");
			if (alreadyExecuted) return;

			Class[] argTypes = EMPTY_ARG_TYPES;
			Object[] args = null;

			if (argumentSections.size > 0) {
				for(int i = 0; i < argumentSections.size; i++) {
					argumentSections.get(i).execute();
				}
				
				argTypes = new Class[argumentSections.size];

				for (int i = 0; i < argumentSections.size; i++) {
					argTypes[i] = argumentSections.get(i).getArgumentType();
					
					if(argTypes[i] == void.class) {
						String m = "void " + (commandObject == null ? "?" : commandObject.getName()) + "." + methodName;
						throw new RuntimeException("Argument return type can't be void - " + m);
					}
				}
			}

			CommandMethod method = null;
			if (findCommandObjectDuringExecution) {
				Iterator<CommandObject> iterator = objectsWithMethod.iterator();
				CommandObject entry = null;
				while (iterator.hasNext()) {
					entry = iterator.next();
					method = entry.getMethod(methodName, argTypes);
					if (method != null) {
						commandObject = entry;
						break;
					}
				}
			}
			else {
				method = commandObject.getMethod(methodName, argTypes);
			}

			if (method == null) throw new RuntimeException("Method does not exist.");

			returnType = method.getReturnType();

			if (argTypes != null) {
				args = new Object[argumentSections.size];

				for (int i = 0; i < argumentSections.size; i++) {
					Argument section = argumentSections.get(i);
					args[i] = section.getArgument();
				}
			}

			argument = method.invoke(commandObject.getObject(), args);

			alreadyExecuted = true;
		}

		@Override
		public boolean isValid() {
			if (!hasObject && !findCommandObjectDuringExecution || !hasMethod) return false;
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
			alreadyExecuted = false;
			findCommandObjectDuringExecution = false;
			hasObject = false;
			hasMethod = false;
			returnType = null;
			argument = null;
			methodName = null;
			commandObject = null;
			objectsWithMethod = null;
			for (int i = argumentSections.size - 1; i >= 0; i--) {
				Pools.free(argumentSections.pop());
			}
		}
	}

	private static class ObjectArgument extends CommandSection implements Argument {

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
		
		@Override
		public void execute() throws Exception {
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
		
		@Override
		public void execute() throws Exception {
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
		
		@Override
		public void execute() throws Exception {
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
		
		@Override
		public void execute() throws Exception {
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
		
		@Override
		public void execute() throws Exception {
		}
	}

}
