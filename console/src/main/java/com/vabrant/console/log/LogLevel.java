
package com.vabrant.console.log;

import com.badlogic.gdx.graphics.Color;

public enum LogLevel {
	INFO(new Color(0xFFFFFFFF)), ERROR(new Color(0XFF0000FF)), DEBUG(new Color(0x00FF00FF));

	private final Color color;

	LogLevel (Color color) {
		this.color = color;
	}

	public Color getColor () {
		return color;
	}
}
