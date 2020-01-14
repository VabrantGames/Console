package com.vabrant.console;

import com.vabrant.console.annotation.ConsoleMethod;
import com.vabrant.console.annotation.ConsoleObject;

public class PrintPrimitives {

	@ConsoleMethod
	public void printF(float f) {
		System.out.println("float: " + f);
	}
	
	@ConsoleMethod
	public void printI(int i) {
		System.out.println("int: " + i);
	}
	
	@ConsoleMethod
	public void printD(double d) {
		System.out.println("double: " + d);
	}
	
	@ConsoleMethod
	public void printL(long l) {
		System.out.println("long: " + l);
	}
	
}
