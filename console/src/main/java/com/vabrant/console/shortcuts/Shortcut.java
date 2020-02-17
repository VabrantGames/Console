package com.vabrant.console.shortcuts;

import com.badlogic.gdx.utils.reflect.Method;

public class Shortcut {
	
	private int packedKeys;
	private Object invokingObject;
	private Method method;
	
	public void set(int packedKeys, Object invokingObject, Method method) {
		this.packedKeys = packedKeys;
		this.invokingObject = invokingObject;
		this.method = method;
	}
	
	public String getName() {
		return method.getName();
	}
	
	public boolean invoke() {
		try {
			method.invoke(invokingObject, null);
			return true;
		}
		catch(Exception e) {
			return false;
		}
	}
	
//	int keybind;
//	String name;
	
//	public int get(int num) {
//		return (keybind >> (8 * num)) & 0xFF;
//	}
	
//	public void set(int[] keys) {
//		keybind = pack(keys);
//	}
	
//	public static int pack(int[] keys) {
//		int keybind = 0;
//		for(int i = 0; i < keys.length; i++) {
//			keybind |= (keys[i] & 0xFF) << (8 * i);
//		}
//		return keybind;
//	}

}
