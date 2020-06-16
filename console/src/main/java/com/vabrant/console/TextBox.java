/*******************************************************************************
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.vabrant.console;

import java.util.regex.Matcher;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFontCache;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.GlyphLayout.GlyphRun;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.FloatArray;
import com.badlogic.gdx.utils.Pools;
import com.badlogic.gdx.utils.Timer.Task;
import com.vabrant.console.commandsections.CommandSection;
import com.vabrant.console.commandsections.DoubleArgument;
import com.vabrant.console.commandsections.ExecutableObjectArgument;
import com.vabrant.console.commandsections.FloatArgument;
import com.vabrant.console.commandsections.IntArgument;
import com.vabrant.console.commandsections.ObjectArgument;
import com.vabrant.console.shortcuts.ConsoleShortcuts;
import com.vabrant.console.shortcuts.ShortcutCommand;
import com.vabrant.console.shortcuts.ShortcutGroup;
import com.vabrant.console.shortcuts.ShortcutListener;

/**
 * @author John Barton
 */

//TODO rename to CommandLine?
@SuppressWarnings("all")
@ShortcutGroup
public class TextBox extends Widget {

	private DebugLogger logger = new DebugLogger(TextBox.class, DebugLogger.DEBUG);

	private static final char[] SECTION_SEPARATORS = {' ', '(', ',', ')'};
	public static final char NULL_CHARACTER = 0x00;

	private final float turboDeleteInterval = 0.02f;
	private final float turboDeleteStartDelay = 0.3f;
	private boolean isTurboDeleting;
	private boolean turboDelete;
	private float turboDeleteTimer;

	private boolean hasErrors;
	private boolean hasRootExecutableSection;
	
	private int cursor;
	private float cursorX;
	private float cursorY;
	
	private boolean textChanged = true;
	private float textHeight;
	private int fontSize = 0;
	private float width = -1;
	private float height = 30;
	
	private final KeyRepeatTask keyRepeatTask = new KeyRepeatTask();
	
	private final Console console;
	private final FloatArray glyphPositions = new FloatArray();
	private TextBoxInput textBoxInput;
	private Array<CommandSection> sections;
	private StringBuilder consoleText = new StringBuilder();
	private final GlyphLayout layout = new GlyphLayout();
	private TextFieldStyle style;
	private BitmapFontCache fontCache;
	private Array<SectionSpecifier> specifiers = new Array<>();
	private Matcher matcher;
	
	public TextBox(Console console) {
		this.console = console;
		
		Skin skin = console.skin;
		style = skin.get(TextFieldStyle.class);
		
		fontCache = style.font.newFontCache();
		textHeight = style.font.getCapHeight() - style.font.getDescent() * 2;
		
		invalidateHierarchy();
		addListener(new ShortcutListener(ConsoleShortcuts.instance.createShortcutManager(this)));
		
		textBoxInput = new TextBoxInput();
		addListener(textBoxInput);
		
		sections = new Array<>(20);
		specifiers.add(MethodSpecifier.create());
		
		matcher = specifiers.first().getPattern().matcher("");
	}

	private void setSectionTextToErrorColor(int index) {
		CommandSection section = sections.get(index);
		String s = consoleText.substring(section.getStartIndex(), section.getEndIndex() + 1);
		
		float[] verts = fontCache.getVertices();
		float color = Color.RED.toFloatBits();
		
		int idx = section.getStartIndex() * 20;
		for(int i = 0; i < s.length(); i++) {
			verts[idx + 2] = color;
			verts[idx + 7] = color;
			verts[idx + 12] = color;
			verts[idx + 17] = color;
			
			idx += 20;
		}
	}
	
	void setSize(Cell cell) {
		cell.height(height).expandY().growX().bottom();
	}
	
	public static float percentOf(float is, float of) {
		return (is * 100f) / of;
	}

	public void clearConsole() {
		consoleText.delete(0, consoleText.length());
		glyphPositions.clear();
		hasErrors = false;
		cursor = 0;
		textChanged = true;
		
		for(int i = sections.size - 1; i >= 0; i--) {
			Pools.free(sections.pop());
		}
		
		logger.debug("Clear");
	}
	
	private void removeSection(int index) {
		Pools.free(sections.removeIndex(index));
		logger.debug("Removed Section", Integer.toString(index));
	}
	
	private void shiftAllSectionIndexes(int startSection, int amount) {
		for(int i = startSection; i < sections.size; i++) {
			sections.get(i).shiftIndexes(amount);
		}
	}

	private void shiftSectionEndIndex(int index, int amount) {
		sections.get(index).shiftEndIndex(amount);
	}
	
	private CommandSection createSection(int start, int end, int position) {
		logger.info("Create Section");
		
		CommandSection section = Pools.obtain(CommandSection.class);
		section.setIndexes(start, end);
		if(position == -1) {
			sections.add(section);
		}
		else {
			sections.insert(position, section);
		}
		return section;
	}
	
	private void setCursorPosition(int position) {
		if(position < 0 || position > consoleText.length()) return;
		cursor = position;
		textChanged = true;
	}

	private void deleteCharacterAtCursorPosition() {
		if(cursor == 0) return;
		logger.debug("DeleteIndex: " + Integer.toString(cursor - 1));
		consoleText.deleteCharAt((cursor - 1));
		
		int sectionIndex = getSectionAtPosition(cursor - 1);
		logger.debug("c: " + cursor);
		logger.debug("s: " + sectionIndex);
		shiftSectionEndIndex(sectionIndex, -1);
		shiftAllSectionIndexes(sectionIndex + 1, -1);
		
		if(checkForDeadSection(sectionIndex)) {
			moveCursorLeftOnePosition();
			return;
		}
		
		checkIfSectionShouldBeRemoved(sectionIndex);
		updateSection(sectionIndex);
		moveCursorLeftOnePosition();
		textChanged = true;
	}
	
	private boolean checkForDeadSection(int index) {
		CommandSection section = sections.get(index);
		
		if(section.getEndIndex() < section.getStartIndex()) {
			removeSection(index);
			return true;
		}
		return false;
	}
	
	private void checkIfSectionShouldBeRemoved(int index) {
		CommandSection section = sections.get(index);

		final String sectionText = consoleText.substring(section.getStartIndex(), section.getEndIndex() + 1);
		final int endOfWhiteSpace = getLeadingWhiteSpaceAmount(sectionText, 0);
		
		//Check if this section has no white space and is not the first section. If so combine it with the previous section if it can do so.
		//e.g.
		//before: [ hello][world]
		//after:  [ helloworld]
		if(endOfWhiteSpace == 0 && sectionText.length() != 1 && index > 0) {
			System.out.println("What the fluke");
			CommandSection previousSection = sections.get(index - 1);
			
			char lastCharOfPreviousSection = consoleText.charAt(previousSection.getEndIndex());
			
			previousSection.setEndIndex(section.getEndIndex());
			removeSection(index);
			return;
		}
		
		//Check if this section is just whitespace and there's a section after this. If so combine those sections.
		///e.g.
		//before: [   ][ bob]
		//after:  [      bob]
		if(endOfWhiteSpace == sectionText.length() && (index + 1) < sections.size) {
			CommandSection nextSection = sections.get(index + 1);
			nextSection.setStartIndex(section.getStartIndex());
			removeSection(index);
			return;
		}
	}

	private int getSectionAtPosition(int position) {
		if(sections.size == 0) return 0;
		if(position == (consoleText.length() - 1)) return sections.size - 1;
		
		int index = position > consoleText.length() - 1 ? position - 1 : position;
		for(int i = 0; i < sections.size; i++) {
			if(index <= sections.get(i).getEndIndex()) return i;
		}
		return sections.size - 1;
	}
	
	private int getLeadingWhiteSpaceAmount(String str, int offset) {
		int amt = 0;
		for(int i = offset; i < str.length(); i++) {
			if(str.charAt(i) != ' ') break;
			amt++;
		}
		return amt;
	}
	
	private void updateSection(int index) {
		CommandSection section = sections.get(index);

		
		final String sectionText = consoleText.substring(section.getStartIndex(), section.getEndIndex() + 1);
		
		int whiteSpaceOffset = getLeadingWhiteSpaceAmount(sectionText, 0);
		
		//All whitespace 
		if(whiteSpaceOffset == sectionText.length()) return;

		//Get the specifier of the section
		SectionSpecifier specifier = section.getSpecifier();
		
		//Check if the current specifier is still valid
		if(specifier != null) {
			matcher.usePattern(specifier.getPattern());
			matcher.reset(sectionText);
			
			//The entire section matches the specifier format
			if(matcher.find(whiteSpaceOffset) && matcher.end() == sectionText.length()) return;
			
			//Move the start pos to where the first non match char is
			whiteSpaceOffset += matcher.end() - 1;
			
			if(!isSeparator(sectionText.charAt(whiteSpaceOffset + 1))) {
				section.setSpecifer(null);
				section.setValid(false);
			}
		}
		
		if(specifier == null) {
			for(SectionSpecifier s : specifiers) {
				matcher.usePattern(s.getPattern());
				matcher.reset(sectionText);
				
				if(matcher.find(whiteSpaceOffset)) {
					
					//We matched the entire thing
					if(matcher.end() == sectionText.length()) {
						specifier = s;
						section.setSpecifer(s);
						section.setValid(true);
						return;
					}
					else {
						//Move the start pos to where the first non match char is
						whiteSpaceOffset += (matcher.end() - 1);
						
						//If the reason we didn't match was because we hit a separator then the match
						//is true because the separator will be moved into it's own section later
						if(isSeparator(sectionText.charAt(whiteSpaceOffset + 1))) {
							section.setSpecifer(s);
							section.setValid(true);
						}
					}
				}
				else {
					logger.debug("no match");
				}
			}
		}
		
		final int originalStartIndex = section.getStartIndex();
		final int originalEndIndex = section.getEndIndex();
		
		for(int i = whiteSpaceOffset; i < sectionText.length(); i++) {
			char c = sectionText.charAt(i);
			
			if(isSeparator(c)) {
				if(c == ' ') {
					int end = originalStartIndex + i - 1;
					section.setEndIndex(end);
					section = createSection(end + 1, originalEndIndex, ++index);
					if(i < sectionText.length() - 1) updateSection(index);
					return;
				}
				
				//Check if the separator is the first character after zero or all whiteSpace.
				//If so then just end the section at the current position.
				//If there are chars after this separator create a section for them
				if(whiteSpaceOffset == i) {
					int end = originalStartIndex + i;
					section.setEndIndex(end);
					
					if(i < sectionText.length() - 1) {
						int ii = originalStartIndex + i + 1;
						section = createSection(ii, originalEndIndex, ++index);
						updateSection(index);
					}
				}
				else {
					int end = originalStartIndex + i - 1;
					section.setEndIndex(end);
					section = createSection(end + 1, end + 1, ++index);
					updateSection(index);
				}
				return;
			}
			
			if(i == (sectionText.length() - 1)) {
				section.setEndIndex(originalEndIndex);
			}
		}
	}
	
	//Ensures that a section is formatted correctly
	private void checkSectionFormat(int index) {
		CommandSection section = sections.get(index);
		
		//Section no longer has characters
		if(section.getEndIndex() < section.getStartIndex()) {
			removeSection(index);
			return;
		}
		
		boolean deadSpace = true;
		final int endIndex = section.getEndIndex();
		final String text = consoleText.substring(section.getStartIndex(), section.getEndIndex() + 1);
		int stringLiterals = 0;
		//specifier 
		for(int i = 0; i < text.length(); i++) {
			char c = text.charAt(i);
			char nextC = (i + 1) > (text.length() - 1) ? NULL_CHARACTER : text.charAt(i + 1);
			
			if(c == '"') stringLiterals++;
			
			if(deadSpace) {
				if(c != ' ' || c == '.' || c == '"') deadSpace = false;
			}
			
			if(!deadSpace) {
				//create a section
				if(nextC == ' ' || nextC == '.') {
					if(stringLiterals == 0 || stringLiterals == 2) {
						int end = section.getStartIndex() + i;
						section.setEndIndex(end);
						section = createSection(end + 1, end + 1, ++index);
						deadSpace = true;
					}
				}
			}
			
			if(nextC == NULL_CHARACTER) {
				section.setEndIndex(endIndex);
			}
		}
	}

	private void updateSections() {
		String text = consoleText.toString();
		
		CommandSection section = null;
		
		if(section == null) {
			section = Pools.obtain(CommandSection.class);
			sections.add(section);
		}
		else {
			section = sections.get(0);
		}
		logger.debug("start", Integer.toString(section.getStartIndex()));
		
		int sectionIndex = 0;
		boolean getNextSection = false;
		boolean deadSpace = true;
		
		for(int i = 0, length = text.length(); i < length; i++) {
			char c = text.charAt(i);
			char nextC = (i + 1) > (length - 1) ? NULL_CHARACTER : text.charAt(i + 1);
			
			if(getNextSection) {
				getNextSection = false;
				sectionIndex++;
			
				//Create section
				if(sectionIndex > (sections.size - 1)) {
					section = Pools.obtain(CommandSection.class);
					sections.add(section);
					section.setStartIndex(i);
					logger.debug("start", Integer.toString(section.getStartIndex()));
				}
				
			}
			
			//Leading spaces are treaded as dead space
			if(deadSpace && nextC != ' ') deadSpace = false;

			if(!deadSpace && nextC == ' ' || !deadSpace && nextC == NULL_CHARACTER) {
				section.setEndIndex(i);
				logger.debug("end", Integer.toString(section.getEndIndex()));
				section = null;
				deadSpace = true;
				getNextSection = true;
			}
			else if(deadSpace && nextC == NULL_CHARACTER) {
				//Remove last section since it's just space characters
			}
		}
	}
	
	private void fixIndexes() {
		
	}

	@ShortcutCommand(keybinds = {Keys.LEFT})
	private void moveCursorLeftOnePosition() {
		setCursorPosition(cursor - 1);
	}
	
	@ShortcutCommand(keybinds = {Keys.RIGHT})
	private void moveCursorRightOnePosition() {
		setCursorPosition(cursor + 1);
	}
	
	@ShortcutCommand(keybinds = {Keys.HOME})
	private void moveCursorToBeginningPosition() {
		setCursorPosition(0);
	}
	
	@ShortcutCommand(keybinds = {Keys.END})
	private void moveCursorToEndPosition() {
		setCursorPosition(consoleText.length());
	}
	
	private boolean isCharacterSupported(char c) {
		//exclude ASCII control character and extended codes
		if(c < 32 || c > 128) return false;
		if(!style.font.getData().hasGlyph(c)) return false;
		return true;
	}
	
	private boolean isSeparator(char c) {
		for (int i = 0; i < SECTION_SEPARATORS.length; i++) {
			if (c == SECTION_SEPARATORS[i]) return true;
		}
		return false;
	}

	private Class getArgumentType(String section) {
		if(section.isEmpty()) return null;
		
		char firstChar = section.charAt(0);
		
		//---------// NOTES //----------//
		// the '.' followed by a letter represents a method specifier. 
		//object.name specifies both an object and method name 
		//.name specifies just a method name with the object name omitted. the object will be found at runtime 
		
		//if the first char is a letter the argument is either ObjectArgument or ExecutableObjectArgument
		if(Character.isLetter(firstChar)) {
			if(section.contains(".")) {
				return ExecutableObjectArgument.class;
			}
			else {
				return ObjectArgument.class;
			}
		}
		else if(Character.isDigit(firstChar)) {
			if(section.length() == 1) {
				return IntArgument.class;
			}
			else if(section.contains(".")) {
				switch(section.charAt(section.length() - 1)) {
					case 'd':
					case 'D':
						return DoubleArgument.class;
					default:
						return FloatArgument.class;
				}
			}
		}
		else if(firstChar == '.') {
			if(section.length() < 2) return null; 
			
			//the start of the section with the the '.' 
			firstChar = section.charAt(1);
			
			if(Character.isLetter(firstChar)) {
				return ExecutableObjectArgument.class;
			}
			else {
				switch(section.charAt(section.length() - 1)) {
					case 'd':
					case 'D':
						return DoubleArgument.class;
					default:
						return FloatArgument.class;
				}
			}
		}
		
		return null;
	}
	
	private void parseInput() throws Exception {
		if(hasErrors) throw new RuntimeException("Command contains errors");
		if(sections.size == 0) return;

	}
	
	private class TextBoxInput extends ClickListener {
		@Override
		public boolean keyDown(InputEvent event, int keycode) {
			switch(keycode) {
				case Keys.ENTER:
					try {
						parseInput();
					}
					catch(Exception e) {
						e.printStackTrace();
					}
					clearConsole();
					break;
				case Keys.BACKSPACE:
					deleteCharacterAtCursorPosition();
					turboDelete = true;
					break;
			}
			
			return super.keyDown(event, keycode);
		}
		
		@Override
		public boolean keyUp(InputEvent event, int keycode) {
			keyRepeatTask.cancel();
			
			switch(keycode) {
				case Keys.BACKSPACE:
					turboDeleteTimer = 0; 
					isTurboDeleting = false;
					turboDelete = false;
					break;
			}
			
			return super.keyDown(event, keycode);
		}
		
		@Override
		public boolean keyTyped(InputEvent event, char character) {
			if(!hasKeyboardFocus()) return false;
			
			if(!isCharacterSupported(character)) return false;
			
			if(sections.size == 0) {
				createSection(0, -1, 0);
			}

			consoleText.insert(cursor, character);

			int sectionIndex = getSectionAtPosition(cursor);
			shiftSectionEndIndex(sectionIndex, 1);
			shiftAllSectionIndexes(sectionIndex + 1, 1);
			updateSection(sectionIndex);
			moveCursorRightOnePosition();
			
			textChanged = true;
			return super.keyTyped(event, character);
		}
	}
	
	@ShortcutCommand(keybinds = {Keys.UP})
	private void logDebug() {
		if(logger == null) return;
		
		System.out.println();
		logger.debug("Full Command [" + consoleText.toString() + ']');
		logger.debug("Amount Of Sections: " + sections.size);
		logger.debug("Section At Cursor: " + (getSectionAtPosition(cursor) + 1));
		logger.debug("Cursor Position: " + cursor);
		
		for(int i = 0; i < sections.size; i++) {
			StringBuilder builder = new StringBuilder(50);
			CommandSection section = sections.get(i);
			builder.append((i + 1) + ":");
			builder.append(" [" + consoleText.substring(section.getStartIndex(), section.getEndIndex() + 1) + ']');
			builder.append(" [" + section.getStartIndex() + ", " + section.getEndIndex() + ']');
			
			String spec = section.getSpecifier() == null ? "null" : section.getSpecifier().getSpecifiedSectionClass().getSimpleName();
			builder.append(" [" + spec + ']');
			logger.debug(builder.toString());
		}
	}
	
	@Override
	public void draw(Batch batch, float parentAlpha) {
		final BitmapFont font = style.font;
		final Color fontcolor = style.fontColor;
		final Drawable background = style.background;
		final Drawable cursor = style.cursor;
		
		float x = getX();
		float y = getY();
		float width = getWidth();
		float height = getHeight();
		
		background.draw(batch, x, y, width, height);
		
		font.setColor(Color.BLACK);
		
		if(textChanged) {
			textChanged = false;
			updateDisplayText();
		}
		
		fontCache.draw(batch);
		cursor.draw(batch, cursorX, cursorY, cursor.getMinWidth(), textHeight);
	}

	//Saves the x's of each char. Used for cursor position
	private void updateDisplayText() {
		Drawable background = style.background;
		BitmapFont font = style.font;
		
		float windowWidth = getWidth() - (background.getLeftWidth() - background.getRightWidth());
		
		float fontX = getX() + background.getLeftWidth();
		
		//Move the baseline to the start of the background texture
		//Center the text inside of the width minus the padding
		float fontY = getY() + font.getCapHeight() + background.getBottomHeight();
		fontY += ((height - background.getTopHeight() - background.getBottomHeight() - font.getCapHeight()) / 2);

		layout.setText(style.font, consoleText, 0, consoleText.length(), style.fontColor, windowWidth, Align.left, false, "");
		fontCache.setText(layout, fontX, fontY);
		glyphPositions.clear();
		
		float x = 0;
		if (layout.runs.size > 0) {
			GlyphRun run = layout.runs.first();
			FloatArray xAdvances = run.xAdvances;
			for (int i = 1, n = xAdvances.size; i < n; i++) {
				glyphPositions.add(x);
				x += xAdvances.get(i);
			}
		} 
		glyphPositions.add(x);
		
		float[] xAdv = glyphPositions.items;
		cursorX = fontX + xAdv[cursor] + font.getData().cursorX;
		cursorY = getY() + background.getBottomHeight(); 
		cursorY += (height - style.background.getTopHeight() - style.background.getBottomHeight() - textHeight) / 2;
	}
	
	@Override
	public void act(float delta) {
		super.act(delta);
		
		if (turboDelete) {
			turboDeleteTimer += delta;
			
			if(!isTurboDeleting) {
				if(turboDeleteTimer < turboDeleteStartDelay) return;
				isTurboDeleting = true;
				turboDeleteTimer = turboDeleteInterval + 1;
			}
				
			if(turboDeleteTimer > turboDeleteInterval) {
				turboDeleteTimer = 0;
				deleteCharacterAtCursorPosition();
			}
		}
	}
	
	private class KeyRepeatTask extends Task {
		int keycode;
		
		@Override
		public void run() {
			textBoxInput.keyDown(null, keycode);
		}
	}
	
	public static class TextBoxStyle {
		public BitmapFont font;
		public Color normalColor;
		public Color errorColor;
		public Drawable background;
	}

}
