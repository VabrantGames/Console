package com.vabrant.console;

public class ShortcutManager {
	
	public int getShortcutOne(int shortcuts) {
		return shortcuts & 0xFF;
	}
	
	public int getShortcutTwo(int shortcuts) {
		return (shortcuts >> 8) & 0xFF;
	}

	public static class Shortcut {
		int keys;
		String name;
		
		public void setKeys(int key1, int key2, int key3, int key4) {
			keys = (key1 & 0xFF) | ((key2 & 0xFF) << 8) | ((key3 & 0xFF) << 16) | ((key4 & 0xFF) << 24);
		}
		
	}

}
