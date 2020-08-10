package com.vabrant.console;

import com.vabrant.console.annotation.ConsoleMethod;
import com.vabrant.console.annotation.ConsoleObject;

public class PrintPrimitives {
	
	@ConsoleMethod
	public void age() {
		System.out.println(25);
	}
	
	@ConsoleMethod
	public int getAge() {
		return 25;
	}

	@ConsoleMethod
	public void print(float f) {
		System.out.println("float: " + f);
	}
	
	@ConsoleMethod
	public void print(int i) {
		System.out.println("int: " + i);
	}
	
	@ConsoleMethod
	public void print(double d) {
		System.out.println("double: " + d);
	}
	
	@ConsoleMethod
	public void print(long l) {
		System.out.println("long: " + l);
	}
	
}
