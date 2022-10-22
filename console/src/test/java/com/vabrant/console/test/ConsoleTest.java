package com.vabrant.console.test;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.MathUtils;
import com.vabrant.console.Console;
import com.vabrant.console.ConsoleCache;
import com.vabrant.console.DebugLogger;

public class ConsoleTest extends ConsoleTestApplication {
	
	static {
		TEST_APP_CLASS = ConsoleTest.class;
		WIDTH = 960;
		HEIGHT = 640;
	}

	Console console;
	ConsoleCache cache;

	@Override
	public void create() {
		super.create();
		
		console = new Console(batch);
		cache = new ConsoleCache();
		cache.setLogLevel(DebugLogger.DEBUG);
		
//		cache.add(new ElectricGuitar(), "bass");
		cache.add(new Print(), "pr");
		cache.addReference(MathUtils.class, "mu");
		cache.addMethod(Print.class, "hello");
		cache.addReference(new Object(), "oo");
		console.setCache(cache);
		
//		console.add("acoustic", new Guitar());
//		console.add("print", new PrintPrimitives());
	}

	@Override
	public void resize(int width, int height) {
		console.resize(width, height);
	}
	
	@Override
	public void update(float delta) {
		console.update(Gdx.graphics.getDeltaTime());
	}
	
	@Override
	public void draw(Batch batch) {
		console.draw();
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void dispose() {
		batch.dispose();
	}

}
