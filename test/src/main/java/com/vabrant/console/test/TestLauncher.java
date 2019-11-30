package com.vabrant.console.test;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class TestLauncher {

	public static void main(String[] args) {
		new LwjglApplication(new ConsoleTestApplicationListener(), getDefaultConfiguration());
	}

	private static LwjglApplicationConfiguration getDefaultConfiguration() {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "ConsoleTest";
		config.width = 960;
		config.height = 640;
		return config;
	}
}
