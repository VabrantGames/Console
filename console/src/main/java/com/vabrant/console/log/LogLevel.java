
package com.vabrant.console.log;

import com.badlogic.gdx.graphics.Color;

public enum LogLevel {
	INFO(Color.BLACK), ERROR(Color.RED), DEBUG(new Color(0x2CC72EFF)), NORMAL(Color.BLACK);

	private Color color;
	private String colorHexString;

	LogLevel (Color color) {
		setColor(color);
	}

	public void setColor (Color color) {
		this.color = color;
		colorHexString = color.toString();
	}

	public Color getColor() {
		return color;
	}

	public String getColorHexString() {
		return colorHexString;
	}

}
