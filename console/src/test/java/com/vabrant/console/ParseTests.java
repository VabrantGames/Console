package com.vabrant.console;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.vabrant.console.annotation.ConsoleMethod;
import com.vabrant.console.annotation.ConsoleObject;
import com.vabrant.console.commandsections.CommandSection;
import com.vabrant.console.commandsections.DoubleArgument;
import com.vabrant.console.commandsections.FloatArgument;
import com.vabrant.console.commandsections.InstanceReferenceArgument;
import com.vabrant.console.commandsections.IntArgument;
import com.vabrant.console.commandsections.LongArgument;
import com.vabrant.console.commandsections.MethodArgument;
import com.vabrant.console.commandsections.MethodArgument.MethodArgumentInfo;
import com.vabrant.console.commandsections.StringArgument;

public class ParseTests {
	
	@BeforeAll
	public static void init() {
		DebugLogger.useSysOut();
	}
	
	@ParameterizedTest
	@ValueSource(strings = {
			"15f", 
			".15f", 
			"15.0f"
	})
	public void FloatTest(String s) {
		FloatArgument arg = new FloatArgument();
		arg.parse(null, s, null);
	}
	
	@ParameterizedTest
	@ValueSource(strings = {
			"100", 
			"0x64",
			"#64",
			"0b01100100"
	})
	public void IntTest(String s) {
		IntArgument arg = new IntArgument();
		arg.parse(null, s, null);
	}
	
	@ParameterizedTest
	@ValueSource(strings = {
			"100.0",
			"100.0d",
			".0d"
	})
	public void DoubleTest(String s) {
		DoubleArgument arg = new DoubleArgument();
		arg.parse(null, s, null);
	}
	
	@ParameterizedTest
	@ValueSource(strings = {
			"100l", 
			"100L"
	})
	public void LongTest(String s) {
		LongArgument arg = new LongArgument();
		arg.parse(null, s, null);
	}
	
	@Test
	public void StringTest() {
		StringArgument arg = new StringArgument();
		arg.parse(null, "Hello World", null);
	}
	
	@Test
	public void ClassReferenceTest() {
		ConsoleCache cache = new ConsoleCache();
		
		Object ob1 = new Object();
		Object ob2 = new Object();
		
		cache.addReference(ob1, "ob1");
		cache.addReference(ob2, "ob2");
		
		InstanceReferenceArgument arg = new InstanceReferenceArgument();
		
		arg.parse(cache, "ob1", null);
		arg.parse(cache, "ob2", null);
	}
	
	@Test
	public void ZeroArgMethodTest() {
		ConsoleCache cache = new ConsoleCache();
		cache.setLogLevel(DebugLogger.DEBUG);
		
		MethodArgument arg = new MethodArgument();
		
		//Test class
		@ConsoleObject
		class Bob {
			@ConsoleMethod
			public void hello() {}
		}
		
		Bob bob = new Bob();
		
		//Adds bob instance and methods to cache
		cache.add(bob, "bob");
		
		arg.parse(cache, ".hello", new MethodArgumentInfo());
	}
	
	@Test
	public void ArgMethodTest() {
		ConsoleCache cache = new ConsoleCache();
		cache.setLogLevel(DebugLogger.DEBUG);
		
		MethodArgument arg = new MethodArgument();

		//Test class
		@ConsoleObject
		class Bob {
			@ConsoleMethod
			public void hello(String s) {
				System.out.println("Hello World");
			}
		}
		
		//Adds bob instance and methods to cache
		cache.add(new Bob(), "bob");
		
		//Mock string argument
		CommandSection stringArgumentSection = new CommandSection();
		stringArgumentSection.setText("Hello World");
		stringArgumentSection.setArgumentType(new StringArgument());
		stringArgumentSection.setReturnObject("Hello World");
		
		MethodArgumentInfo info = new MethodArgumentInfo();
		info.addArgumentSection(stringArgumentSection);
		
		arg.parse(cache, ".hello", info);
	}

}
