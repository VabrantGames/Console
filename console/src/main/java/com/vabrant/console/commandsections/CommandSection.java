package com.vabrant.console.commandsections;

import com.badlogic.gdx.utils.Pool.Poolable;

public class CommandSection implements Poolable {
	private int start;
	private int end;
	private boolean isValid;
	
	public void setIndexes(int start, int end) {
		this.start = start;
		this.end = end;
	}
	
	public void shitfIndexes(int amount) {
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
		start = 0;
		end = 0;
		isValid = false;
	}

}
