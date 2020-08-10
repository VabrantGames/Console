package com.vabrant.console;

import com.vabrant.console.annotation.ConsoleMethod;
import com.vabrant.console.annotation.ConsoleObject;

@ConsoleObject
public class Print {
	
	public static void hello() {
		System.out.println("Hello World bob");
	}

	@ConsoleMethod
	public void print(int i) {
		System.out.println("Printed: " + i);
	}
	
	@ConsoleMethod
	public void print(float f) {
		System.out.println("Printed: " + f);
	}
	
	@ConsoleMethod 
	public void print(double d) {
		System.out.println("Printed: " + d);
	}
	
	@ConsoleMethod
	public void print(long l) {
		System.out.println("Printed: " + l);
	}
	
	@ConsoleMethod
	public void print(String s) {
		System.out.println("Printed: " + s);
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
}
