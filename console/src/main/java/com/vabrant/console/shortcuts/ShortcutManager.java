package com.vabrant.console.shortcuts;

import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.reflect.Method;

public class ShortcutManager {
	
	private Object invokeObject;
	private IntMap<Method> shortcuts;
	private Method invokeMethod;
	
	public ShortcutManager(Object invokeObject) {
		this.invokeObject = invokeObject;
		shortcuts = new IntMap<>(10);
	}
	
	public void addShortcut(int keybind, Method method) {
		shortcuts.put(keybind, method);
	}

	public boolean hasShortcut(int keybind) {
		invokeMethod = null;
		invokeMethod = shortcuts.get(keybind);
		return invokeMethod != null;
	}
	
	public void invoke() {
		try {
			invokeMethod.invoke(invokeObject, null);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

}
