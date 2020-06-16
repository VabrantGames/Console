package com.vabrant.console;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;

public class ConsoleTest extends ConsoleTestApplication {
	
	static {
		ConsoleTestApplication.TEST_APP_CLASS = ConsoleTest.class;
		ConsoleTestApplication.WIDTH = 960;
		ConsoleTestApplication.HEIGHT = 640;
	}

	Console console;

	@Override
	public void create() {
		super.create();
		
		console = new Console(batch);
//		console.add("bass", new ElectricGuitar());
//		console.add("acoustic", new Guitar());
//		console.add("print", new PrintPrimitives());

//		console.printObjects();
//		console.printMethods();
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
