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

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFontCache;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.GlyphLayout.GlyphRun;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
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
//import com.badlogic.gdx.utils.StringBuilder;
import com.vabrant.console.commandsections.Argument;
import com.vabrant.console.commandsections.ArgumentSection;
import com.vabrant.console.commandsections.CommandSection;
import com.vabrant.console.commandsections.DoubleArgument;
import com.vabrant.console.commandsections.ExecutableObjectArgument;
import com.vabrant.console.commandsections.FloatArgument;
import com.vabrant.console.commandsections.IntArgument;
import com.vabrant.console.commandsections.ObjectArgument;
import com.vabrant.console.commandsections.SeparatorSection;
import com.vabrant.console.shortcuts.ConsoleShortcuts;
import com.vabrant.console.shortcuts.ShortcutCommand;
import com.vabrant.console.shortcuts.ShortcutGroup;
import com.vabrant.console.shortcuts.ShortcutListener;

/**
 * @author John
 */

@SuppressWarnings("all")
@ShortcutGroup
public class TextBox extends Widget {

	private DebugLogger logger = DebugLogger.getLogger(TextBox.class, DebugLogger.DEVELOPMENT_DEBUG);

	private static final char[] SECTION_SPECIFIERS = {' ', '.'};
	private static final char[] separators = {' '};
	public static final char NULL_CHARACTER = 0x00;

	private final float turboDeleteInterval = 0.02f;
	private final float turboDeleteStartDelay = 0.3f;
	private boolean isTurboDeleting;
	private boolean turboDelete;
	private float turboDeleteTimer;

	private boolean hasErrors;
	private boolean hasRootExecutableSection;
	
	private int cursor;
	
	private boolean textChanged = true;
	private boolean buildDisplayString;
	private float textHeight;
	private int visibleTextStart;
	private int visibleTextEnd;
	private float fontOffset;
	private float textOffset;
	private int maxLength; 
	private float renderOffset;
	
	private final KeyRepeatTask keyRepeatTask = new KeyRepeatTask();
	
	private int indexOfSectionAtCursorPosition;
	private final Console console;
	private final FloatArray glyphPositions = new FloatArray();
	private TextBoxInput textBoxInput;
	private Array<CommandSection> sections;
	private StringBuilder consoleText = new StringBuilder();
	private final GlyphLayout layout = new GlyphLayout();
	private TextFieldStyle style;
	private BitmapFontCache fontCache;
	
	public TextBox(Console console) {
		this.console = console;
		
		Skin skin = console.skin;
		style = skin.get(TextFieldStyle.class);
		style.font.getData().markupEnabled = true;
		
		fontCache = style.font.newFontCache();
		
		textHeight = style.font.getCapHeight() - style.font.getDescent() * 2;
		
		invalidateHierarchy();
		addListener(new ShortcutListener(ConsoleShortcuts.instance.createShortcutManager(this)));
		
		textBoxInput = new TextBoxInput();
		addListener(textBoxInput);
		setSize(getPrefWidth(), getPrefHeight());
		
		sections = new Array<>(20);
	}

	public void clearConsole() {
		consoleText.delete(0, consoleText.length());
		glyphPositions.clear();
		hasErrors = false;
		cursor = 0;
		textChanged = true;
		indexOfSectionAtCursorPosition = 0;
		
		for(int i = sections.size - 1; i >= 0; i--) {
			Pools.free(sections.pop());
		}
		
		logger.debug("Clear");
	}
	
	private void removeSection(int index) {
		if(index > sections.size - 1) return;
		Pools.free(sections.removeIndex(index));
		setSectionAtCursorPosition();
	}
	
	private void shiftSectionIndexes(int startSection, int amount) {
		for(int i = startSection; i < sections.size; i++) {
			sections.get(i).shiftIndexes(amount);
		}
	}
	
	private void offsetSectionEndIndex(int amount) {
		if(sections.size == 0) return;
		CommandSection section = sections.get(indexOfSectionAtCursorPosition);
		section.setEndIndex(section.getEndIndex() + amount);
	}
	
	private CommandSection createSection(int start, int end, int position) {
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
		setSectionAtCursorPosition();
		textChanged = true;
	}

	private void deleteCharacterAtCursorPosition() {
		if(cursor == 0) return;
		consoleText.deleteCharAt((cursor - 1));
		offsetSectionEndIndex(-1);
		shiftSectionIndexes(indexOfSectionAtCursorPosition + 1, -1);
		checkSectionFormat(indexOfSectionAtCursorPosition);
		moveCursorLeftOnePosition();
		updateDisplayText();
		textChanged = true;
	}
	
	private void setSectionAtCursorPosition() {
		if(sections.size == 0) {
			indexOfSectionAtCursorPosition = 0;
			return;
		}
		
		indexOfSectionAtCursorPosition = sections.size - 1;
		int cursorIndex = (cursor > (consoleText.length() - 1)) ? cursor - 1 : cursor;
		for(int i = 0; i < sections.size; i++) {
			CommandSection s = sections.get(i);
			if(cursorIndex >= s.getStartIndex() && cursorIndex <= s.getEndIndex()) {
				indexOfSectionAtCursorPosition = i;
				break;
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
		
		final int endIndex = section.getEndIndex();
		final String text = consoleText.substring(section.getStartIndex(), section.getEndIndex() + 1);
		boolean deadSpace = true;
		
		for(int i = 0; i < text.length(); i++) {
			char c = text.charAt(i);
			char nextC = (i + 1) > (text.length() - 1) ? NULL_CHARACTER : text.charAt(i + 1);
			
			if(deadSpace) {
				if(c != ' ' || c == '.') deadSpace = false;
			}
			
			if(!deadSpace) {
				//create a section
				if(nextC == ' ' || nextC == '.') {
					int end = section.getStartIndex() + i;
					section.setEndIndex(end);
					section = createSection(end + 1, end + 1, ++index);
					deadSpace = true;
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
		for (int i = 0; i < separators.length; i++) {
			if (c == separators[i]) return true;
		}
		return false;
	}
	
	private boolean isSectionSpecifier(char c) {
		for(int i = 0; i < SECTION_SPECIFIERS.length; i++) {
			if(c == SECTION_SPECIFIERS[i]) return true;
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
				System.out.println("Create initial section");
				createSection(0, -1, 0);
			}

			consoleText.insert(cursor, character);

			//Increase section size by 1
			offsetSectionEndIndex(1);
			
			//Shift all sections after the currently selected section by 1
			shiftSectionIndexes(indexOfSectionAtCursorPosition + 1, 1);
			
			if(isSectionSpecifier(character)) checkSectionFormat(indexOfSectionAtCursorPosition);

			moveCursorRightOnePosition();
			
			updateDisplayText();
			
			textChanged = true;
			return super.keyTyped(event, character);
		}
	}
	
	@ShortcutCommand(keybinds = {Keys.UP})
	private void logDebug() {
		if(logger == null) return;
		
		System.out.println();
		logger.devDebug("Full Command [" + consoleText.toString() + ']');
		logger.devDebug("Amount Of Sections: " + sections.size);
		logger.devDebug("Section At Cursor: " + (indexOfSectionAtCursorPosition + 1));
		
		for(int i = 0; i < sections.size; i++) {
			StringBuilder builder = new StringBuilder(50);
			CommandSection section = sections.get(i);
			builder.append((i + 1) + ":");
			builder.append(" [" + consoleText.substring(section.getStartIndex(), section.getEndIndex() + 1) + ']');
			builder.append(" [" + section.getStartIndex() + ", " + section.getEndIndex() + ']');
			logger.debug(builder.toString());
		}
	}
	
	@Override
	public void draw(Batch batch, float parentAlpha) {
		final BitmapFont font = style.font;
		final Color fontcolor = style.fontColor;
		final Drawable background = style.background;
		
		Color color = getColor();
		float x = getX();
		float y = getY();
		float width = getWidth();
		float height = getHeight();
		
		float textY = getTextY(font, background);
		
		batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);
		float bgLeftWidth = 0, bgRightWidth = 0;
		background.draw(batch, x, y, width, height);
		
		if(buildDisplayString) {
			buildDisplayString = false;
		}
		
		fontCache.draw(batch);
		
		if(textChanged) {
			textChanged = false;
			
			updateDisplayText();
//			calculateOffsets();
			
//			fontCache.addText(consoleText, x + textOffset, y, visibleTextStart, visibleTextEnd, 0, Align.left, false);
//			Color fontColor = style.fontColor;
//			fontCache.setColor(fontColor.r, fontColor.g, fontColor.b, fontColor.a * color.a * parentAlpha);
		}
		
		fontCache.draw(batch);
		
//		font.setColor(fontColor.r, fontColor.g, fontColor.b, fontColor.a * color.a * parentAlpha);
		
//		font.draw(batch, myDisplayText, x + background.getLeftWidth(), y + textY, 0, myDisplayText.length(), 0, Align.left, false);
//		font.draw(batch, displayText, x + background.getLeftWidth(), y + textY);
//		font.draw(batch, layout, x + background.getLeftWidth(), y + textY);
		
//		x = text.length() == 0 ? 0 : getX() + background.getLeftWidth() + glyphPositions.get(text.length() - 1) +font.getData().cursorX + fontOffset;
		
//		x = getX() + background.getLeftWidth() + font.getData().cursorX + fontOffset + layout.width;
//		float off = text.length() == 0 ? 0 : glyphPositions.peek() - glyphPositions.first() + font.getData().cursorX;
		
//		Drawable cursorDrawable = style.cursor;
//		cursorDrawable.draw(batch, x + textOffset + off, y + textY - textHeight - font.getDescent(), cursorDrawable.getMinWidth(), textHeight);
//		drawText(batch, font, x + background.getLeftWidth(), y + textY);
//		drawCursor(style.cursor, batch, font, x + background.getLeftWidth(), y + textY);
	}
	
	protected void drawText (Batch batch, BitmapFont font, float x, float y) {
//		font.draw(batch, displayText, x + textOffset, y, visibleTextStart, visibleTextEnd, 0, Align.left, false);
//		fontCache.clear();
//		fontCache.addText(displayText, x + textOffset, y, visibleTextStart, visibleTextEnd, 0, Align.left, false);
		fontCache.setPosition(x + textOffset, y);
		fontCache.draw(batch, 1);
	}
	
	protected void drawCursor (Drawable cursorPatch, Batch batch, BitmapFont font, float x, float y) {
		cursorPatch.draw(
				batch,
				x + textOffset + glyphPositions.get(cursor) - glyphPositions.get(visibleTextStart) + fontOffset + font.getData().cursorX,
				y - textHeight - font.getDescent(), cursorPatch.getMinWidth(), textHeight
		);
	}
	
	private void calculateOffsets() {
		//width of the window
		float visibleWidth = getWidth();
		
		Drawable background = style.background;
		
		//subtract left padding and right padding
		if (background != null) visibleWidth -= (background.getLeftWidth() + background.getRightWidth());

		//not actual glyph count. actual is this subtracted by one
		int glyphCount = glyphPositions.size;
		
		logger.debug("Count: " + glyphCount);
		
		//this is the xadvances of the glyphs added together
		float[] glyphPositions = this.glyphPositions.items;

		// Check if the cursor has gone out the left or right side of the visible area and adjust renderOffset.
		//keeps the cursor in the correct position when the text over fills the width of the text box
		float distance = glyphPositions[Math.max(0, cursor - 1)] + renderOffset;
		if (distance <= 0) {
			renderOffset -= distance;
		}
		else {
			int index = Math.min(glyphCount - 1, cursor + 1);
			float minX = glyphPositions[index] - visibleWidth;
			if (-renderOffset < minX) renderOffset = -minX;
		}

		// Prevent renderOffset from starting too close to the end, eg after text was deleted.
		float maxOffset = 0;
		float width = glyphPositions[glyphCount - 1];
		for (int i = glyphCount - 2; i >= 0; i--) {
			float x = glyphPositions[i];
			if (width - x > visibleWidth) break;
			maxOffset = x;
		}
		if (-renderOffset > maxOffset) renderOffset = -maxOffset;

		// calculate first visible char based on render offset
		visibleTextStart = 0;
		float startX = 0;
		for (int i = 0; i < glyphCount; i++) {
			if (glyphPositions[i] >= -renderOffset) {
				visibleTextStart = i;
				startX = glyphPositions[i];
				break;
			}
		}

		// calculate last visible char based on visible width and render offset
		int end = visibleTextStart + 1;
		float endX = visibleWidth - renderOffset;
		for (int n = Math.min(consoleText.length(), glyphCount); end <= n; end++)
			if (glyphPositions[end] > endX) break;
		visibleTextEnd = Math.max(0, end - 1);

		textOffset = startX + renderOffset;
	}
	
	@Override
	public float getPrefHeight () {
		float topAndBottom = 0;
		float minHeight = 0;
		
		if (style.background != null) {
			//max(0, top + bottom padding)
			topAndBottom = Math.max(topAndBottom, style.background.getBottomHeight() + style.background.getTopHeight());
			//max(0, height of texture)
			minHeight = Math.max(minHeight, style.background.getMinHeight());
		}
		
		//max(padding, height of texture)
		return Math.max(topAndBottom, minHeight);
	}
	
	protected float getTextY (BitmapFont font, Drawable background) {
		float height = getHeight();
		float textY = textHeight / 2 + font.getDescent();
		if (background != null) {
			float bottom = background.getBottomHeight();
			textY = textY + (height - background.getTopHeight() - bottom) / 2 + bottom;
		} else {
			textY = textY + height / 2;
		}
		if (font.usesIntegerPositions()) textY = (int)textY;
		return textY;
	}
	
	boolean withinMaxLength (int size) {
		return maxLength <= 0 || size < maxLength;
	}

	public void setMaxLength (int maxLength) {
		this.maxLength = maxLength;
	}

	public int getMaxLength () {
		return this.maxLength;
	}
	
//	public void paste(String content) {
//		if (content == null) return;
//		StringBuilder buffer = new StringBuilder();
//		int textLength = text.length();
//		BitmapFontData data = style.font.getData();
//		for (int i = 0, n = content.length(); i < n; i++) {
//			if (!withinMaxLength(textLength + buffer.length())) break;
//			char c = content.charAt(i);
//			buffer.append(c);
//		}
//		content = buffer.toString();
//
//		text = insert(cursor, content, text);
//		updateDisplayText();
//		cursor += content.length();
//	}

	//Saves the x's of each char. Used for cursor position
	private void updateDisplayText() {
		Drawable background = style.background;
		float windowWidth = getWidth() - (background.getLeftWidth() - background.getRightWidth());
		
		layout.setText(style.font, consoleText, 0, consoleText.length(), style.fontColor, windowWidth / 2, Align.left, false, "");
		
		BitmapFont font = style.font;
		
		float fontX = getX() + background.getLeftWidth();
		float fontY = 0 + getY() + font.getCapHeight() + (background.getMinHeight() / 2 - background.getBottomHeight() - background.getTopHeight());
		
		
		
		fontCache.setText(layout, fontX, fontY);
		
		
		glyphPositions.clear();
		float x = 0;

//		fontCache.getVertices()
		
		if (layout.runs.size > 0) {
			GlyphRun run = layout.runs.first();
			FloatArray xAdvances = run.xAdvances;
			fontOffset = xAdvances.first();
			for (int i = 1, n = xAdvances.size; i < n; i++) {
				glyphPositions.add(x);
				x += xAdvances.get(i);
			}
		} 
		else {
			fontOffset = 0;
		}
		glyphPositions.add(x);

//		visibleTextStart = Math.min(visibleTextStart, glyphPositions.size - 1);
//		visibleTextEnd = MathUtils.clamp(visibleTextEnd, visibleTextStart, glyphPositions.size - 1);
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
