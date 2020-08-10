package de.tomgrill.gdxtesting;

import org.junit.BeforeClass;
import org.junit.Test;

import com.vabrant.console.ConsoleCache;
import com.vabrant.console.DebugLogger;
import com.vabrant.console.annotation.ConsoleMethod;
import com.vabrant.console.annotation.ConsoleObject;
import com.vabrant.console.commandsections.InstanceReferenceArgument;
import com.vabrant.console.commandsections.DoubleArgument;
import com.vabrant.console.commandsections.FloatArgument;
import com.vabrant.console.commandsections.IntArgument;
import com.vabrant.console.commandsections.LongArgument;
import com.vabrant.console.commandsections.MethodArgument;
import com.vabrant.console.commandsections.StringArgument;

public class ParseTests {
	
	@BeforeClass
	public static void init() {
		DebugLogger.useSysOut();
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
