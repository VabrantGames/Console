
package com.vabrant.console.test;

import com.vabrant.console.annotation.ConsoleMethod;
import com.vabrant.console.annotation.ConsoleObject;

@ConsoleObject
public class TestMethods {

	@ConsoleMethod
	public void hello () {
		System.out.println("Hello");
	}

	@ConsoleMethod
	public void print (int i) {
		System.out.println("int: " + i);
	}

	@ConsoleMethod
	public void print (long l) {
		System.out.println("long: " + l);
	}

	@ConsoleMethod
	public void print (float f) {
		System.out.println("float: " + f);
	}

	@ConsoleMethod
	public void print (double d) {
		System.out.println("double: " + d);
	}

	@ConsoleMethod
	public void print (boolean b) {
		System.out.println("boolean: " + b);
	}

	@ConsoleMethod
	public int add (int x1, int x2) {
		return x1 + x2;
	}

	@ConsoleMethod
	public int getInt () {
		return 5;
	}
}
