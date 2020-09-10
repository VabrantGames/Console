package com.vabrant.console;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.vabrant.console.annotation.ConsoleMethod;
import com.vabrant.console.annotation.ConsoleObject;

public class GuiTest extends ApplicationAdapter {
	
	public static void main(String[] args) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = 960;
		config.height = 640;
		new LwjglApplication(new GuiTest(), config);
	}

	private Console console;
	private ConsoleCache cache;
	private SpriteBatch batch;

	@Override
	public void create() {
		super.create();
		batch = new SpriteBatch();
		console = new Console(batch, new Skin(Gdx.files.internal("orangepeelui/uiskin.json")));
		cache = new ConsoleCache();
		cache.setLogLevel(DebugLogger.DEBUG);
		cache.add(new ElectricGuitar(), "elec");
		cache.add(new BassGuitar(), "bass");
		cache.add(new Print(), "print");
		cache.addReference(MathUtils.class, "mu");
		cache.addMethod(MathUtils.class, "random");
		cache.addMethod(Print.class, "hello");
		console.setCache(cache);
	}

	@Override
	public void resize(int width, int height) {
		console.resize(width, height);
	}
	
	@Override
	public void render() {
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		console.update(Gdx.graphics.getDeltaTime());
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
	
	@ConsoleObject
	private static class Print {
		
		public static void hello() {
			System.out.println("Hello World");
		}
		
		@ConsoleMethod
		public void print(int i) {
			System.out.println("Printed int: " + i);
		}
		
		@ConsoleMethod
		public void print(float f) {
			System.out.println("Printed float: " + f);
		}
		
		@ConsoleMethod 
		public void print(double d) {
			System.out.println("Printed double: " + d);
		}
		
		@ConsoleMethod
		public void print(long l) {
			System.out.println("Printed long: " + l);
		}
		
		@ConsoleMethod
		public String print(String s) {
			return "Printed String: " + s;
		}
		
		@ConsoleMethod
		public int addInts(int a, int b, int c) {
			int sum = a + b + c;
			System.out.println("sum: " + sum);
			return sum;
		}
		
		@ConsoleMethod
		public float add(int a, float b) {
			return a + b;
		}
		
		@ConsoleMethod
		public int getAge() {
			return 26;
		}
		
		@ConsoleMethod
		public int multBy2(int num) {
			System.out.println("MultBy2");
			return num * 2;
		}
		
		@ConsoleMethod
		public int mult(int value, int amount) {
			return value * amount;
		}
		
		@ConsoleMethod
		public void print(Object o) {
			System.out.println("Printed: " + o.getClass().getSimpleName());
		}
		
		@ConsoleMethod
		public void print(int i, int i2, int i3, int i4, long l, float f, double d, String s) {
			StringBuilder b = new StringBuilder();
			b.append("Int: " + i);
			b.append('\n');
			b.append("Int: " + i2);
			b.append('\n');
			b.append("Int: " + i3);
			b.append('\n');
			b.append("Int: " + i4);
			b.append('\n');
			b.append("Long: " + l);
			b.append('\n');
			b.append("Float: " + f);
			b.append('\n');
			b.append("Double: " + d);
			b.append('\n');
			b.append("String: " + s);
			b.append('\n');
			System.out.println(b);
		}
		
		@ConsoleMethod
		public void printFloat(float f) {
			System.out.println("Printed Float: " + f);
		}
		
		@ConsoleMethod
		public void printDouble(double d) {
			System.out.println("Printed Double: " + d);
		}

		@ConsoleMethod
		public void printLong(long l) {
			System.out.println("Printed Long: " + l);
		}
	}
	
	@ConsoleObject
	private static class Guitar {
		
		private final String name;
		private final int amountOfStrings;
		
		public Guitar(int amountOfStrings) {
			this.amountOfStrings = amountOfStrings;
			this.name = this.getClass().getSimpleName();
		}
		
		@ConsoleMethod
		public void pluck() {
			System.out.println("Plucked guitar");
		}
		
		@ConsoleMethod
		public void pluck(int amount) {
			System.out.println("Plucked guitar " + amount + " times");
		}
		
		@ConsoleMethod
		public int amountOfStrings() {
			System.out.println("AmountOfStrings: " + amountOfStrings);
			return 6;
		}
		
		@Override
		public String toString() {
			return name;
		}
		
	}

	@ConsoleObject
	private static class ElectricGuitar extends Guitar {
		private ElectricGuitar() {
			super(6);
		}
	}
	
	@ConsoleObject
	private static class BassGuitar extends Guitar {
		private BassGuitar() {
			super(4);
		}
	}
}
