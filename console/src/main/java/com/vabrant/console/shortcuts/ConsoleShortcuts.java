package com.vabrant.console.shortcuts;

import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.Method;

public class ConsoleShortcuts extends InputAdapter{
	
	public static final ConsoleShortcuts instance = new ConsoleShortcuts();
	
	public static final int MAX_KEY_COMBINATION = 4;
	
	private int packedKeys;
	private final Array<ShortcutManager> managers = new Array<>(5);
	private final IntArray pressedKeys = new IntArray(MAX_KEY_COMBINATION);
	private final IntMap<Method> allShortcuts = new IntMap<>();
	
	private ConsoleShortcuts() {
	}
	
	public int getPackedKeys() {
		return packedKeys;
	}
	
	public void clear() {
		packedKeys = 0;
		pressedKeys.clear();
	}
	
	public <T> ShortcutManager createShortcutManager(T group) {
		if(!group.getClass().isAnnotationPresent(ShortcutGroup.class)) return null;
		
		ShortcutManager manager = new ShortcutManager(group);
		Method[] methods = ClassReflection.getDeclaredMethods(group.getClass());
		
		for(Method m : methods) {
			addShortcut(manager, m);
		}

		managers.add(manager);
		
		return manager;
	}
	
	private void addShortcut(ShortcutManager manager, Method method) {
		if(!method.isAnnotationPresent(ShortcutCommand.class)) return;

		//only 0 arg methods
		if(method.getParameterTypes().length > 0) {
			System.out.println("Method must have no arguments");
			return;
		}
		
		ShortcutCommand shortcut = method.getDeclaredAnnotation(ShortcutCommand.class).getAnnotation(ShortcutCommand.class);
		int[] keybinds = shortcut.keybinds();
		if(keybinds.length > MAX_KEY_COMBINATION) {
			System.out.println("Too many keys");
			return;
		}
		
		int packed = packKeys(keybinds);
		if(allShortcuts.containsKey(packed)) {
			System.out.println("Shortcut already added");
			return;
		}
		
		method.setAccessible(true);
		
		manager.addShortcut(packed, method);
	}

	@Override
	public boolean keyDown(int keycode) {
		if(pressedKeys.size < MAX_KEY_COMBINATION) {
			pressedKeys.add(keycode);
			packedKeys = packKeys(pressedKeys.toArray());
		}
		return false;
	}
	
	@Override
	public boolean keyUp(int keycode) {
		if(pressedKeys.removeValue(keycode)) {
			packedKeys = packKeys(pressedKeys.toArray());
		}
		return false;
	}
	
	public static int packKeys(int[] keys) {
		int keybind = 0;
		for(int i = 0; i < keys.length; i++) {
			keybind |= (keys[i] & 0xFF) << (8 * i);
		}
		return keybind;
	}

}
