package com.vabrant.console.commandsections;

import com.badlogic.gdx.utils.Pool.Poolable;
import com.vabrant.console.SectionSpecifier;
import com.vabrant.console.SectionType;

public class CommandSection implements Poolable {
	
	private boolean isValid;
	private int start = -1;
	private int end = -1;
	private BasicArgument<?> argument;
	private SectionSpecifier specifier;
	
	public void setSpecifer(SectionSpecifier specifier) {
		this.specifier = specifier;
	}
	
	public SectionSpecifier getSpecifier() {
		return specifier;
	}
	
	public void setArgument(BasicArgument<?> argument) {
		this.argument = argument;
	}
	
	public BasicArgument<?> getArgument() {
		return argument;
	}
	
	public void setIndexes(int start, int end) {
		this.start = start;
		this.end = end;
	}
	
	public void shiftIndexes(int amount) {
		start += amount;
		end += amount;
	}
	
	public void setStartIndex(int index) {
		start = index;
	}
	
	public int getStartIndex() {
		return start;
	}
	
	public void shiftStartIndex(int amount) {
		start += amount;
	}
	
	public void setEndIndex(int index) {
		end = index;
	}
	
	public int getEndIndex() {
		return end;
	}
	
	public void shiftEndIndex(int amount) {
		end += amount;
	}
	
	public void setValid(boolean isValid) {
		this.isValid = isValid;
	}
	
	public boolean isValid() {
		return isValid;
	}
	
	@Override
	public void reset() {
		start = -1;
		end = -1;
		isValid = false;
	}

}
