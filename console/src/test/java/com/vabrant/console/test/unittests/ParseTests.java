package com.vabrant.console.test.unittests;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.vabrant.console.ConsoleCache;
import com.vabrant.console.annotation.ConsoleMethod;
import com.vabrant.console.annotation.ConsoleObject;
import com.vabrant.console.commandsections.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class ParseTests {

	private static Application application;

	@BeforeAll
	public static void init() {
		application = new HeadlessApplication(new ApplicationAdapter() {});
//		DebugLogger.useSysOut();
	}
	
	@Test
	public void FloatTest() {
		FloatArgument arg = new FloatArgument();
		arg.parse(null, "15f");
		arg.parse(null, ".15f");
		arg.parse(null, "15.0f");
	}
	
	@Test
	public void IntTest() {
		IntArgument arg = new IntArgument();
		arg.parse(null, "100");
		arg.parse(null, "0x64");
		arg.parse(null, "#64");
		arg.parse(null, "0b01100100");
	}
	
	@Test
	public void DoubleTest() {
		DoubleArgument arg = new DoubleArgument();
		arg.parse(null, "100.0");
		arg.parse(null, "100.0d");
		arg.parse(null, ".0d");
		
	}
	
	@Test
	public void LongTest() {
		LongArgument arg = new LongArgument();
		arg.parse(null, "100l");
		arg.parse(null, "100L");
	}
	
	@Test
	public void StringTest() {
		StringArgument arg = new StringArgument();
		arg.parse(null, "Hello World");
	}
	
	@Test
	public void ClassReferenceTest() {
		ConsoleCache cache = new ConsoleCache();
		
		Object ob1 = new Object();
		Object ob2 = new Object();
		
		cache.addReference(ob1, "ob1");
		cache.addReference(ob2, "ob2");
		
		InstanceReferenceArgument arg = new InstanceReferenceArgument();
		
		arg.parse(cache, "ob1");
		arg.parse(cache, "ob2");
	}
	
	@Test
	public void MethodTest() {
		ConsoleCache cache = new ConsoleCache();
		MethodArgument arg = new MethodArgument();
		
		@ConsoleObject
		class Bob {
			@ConsoleMethod
			public void hello() {}
		}
		
		Bob bob = new Bob();
		
		cache.add(bob, "bob");
		
		arg.parse(cache, ".hello");
	}

}
