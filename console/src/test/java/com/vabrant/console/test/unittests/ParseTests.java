package com.vabrant.console.test.unittests;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.vabrant.console.ConsoleCache;
import com.vabrant.console.annotation.ConsoleMethod;
import com.vabrant.console.annotation.ConsoleObject;
import com.vabrant.console.parsers.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class ParseTests {

	private static ConsoleCacheAndStringInput data;
	private static Application application;

	@BeforeAll
	public static void init() {
		application = new HeadlessApplication(new ApplicationAdapter() {});
		data = new ConsoleCacheAndStringInput();
	}
	
	@Test
	void FloatTest() {
		FloatArgumentParser parser = new FloatArgumentParser();

		parser.parse(data.setText("15f"));
		parser.parse(data.setText(".15f"));
		parser.parse(data.setText("15.0f"));
	}
	
	@Test
	void IntTest() {
		IntArgumentParser parser = new IntArgumentParser();

		parser.parse(data.setText("100"));
		parser.parse(data.setText("0x64"));
		parser.parse(data.setText("#64"));
		parser.parse(data.setText("0b01100100"));
	}
	
	@Test
	void DoubleTest() {
		DoubleArgumentParser parser = new DoubleArgumentParser();

		parser.parse(data.setText("100.0"));
		parser.parse(data.setText("100.0d"));
		parser.parse(data.setText(".0d"));
	}
	
	@Test
	void LongTest() {
		LongArgumentParser parser = new LongArgumentParser();

		parser.parse(data.setText("100l"));
		parser.parse(data.setText("100L"));
	}
	
	@Test
	public void ClassReferenceTest() {
		ConsoleCache cache = new ConsoleCache();

		data.setConsoleCache(cache);

		Object ob1 = new Object();
		Object ob2 = new Object();

		cache.addReference(ob1, "ob1");
		cache.addReference(ob2, "ob2");

		InstanceReferenceParser arg = new InstanceReferenceParser();

		arg.parse(data.setText("ob1"));
		arg.parse(data.setText("ob2"));

		data.setConsoleCache(null);
	}
	
	@Test
	public void MethodTest() {
		ConsoleCache cache = new ConsoleCache();
		MethodArgumentParser arg = new MethodArgumentParser();

		data.setConsoleCache(cache);

		@ConsoleObject
		class Bob {
			@ConsoleMethod
			public void hello() {}
		}

		Bob bob = new Bob();

		cache.add(bob, "bob");

		arg.parse(data.setText(".hello"));

		data.setConsoleCache(null);
	}

}
