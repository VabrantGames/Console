
package com.vabrant.console.test;

import com.badlogic.gdx.graphics.Color;
import com.vabrant.console.commandextension.annotation.ConsoleCommand;
import com.vabrant.console.commandextension.annotation.ConsoleReference;

@ConsoleReference
public class TestMethods {

	@ConsoleCommand(
		successMessage = "Hello? World?"
	)
	public void hello () {
		System.out.println("Hello");
	}

	@ConsoleCommand
	public void print (int i) {
		System.out.println("int: " + i);
	}

	@ConsoleCommand
	public void print (long l) {
		System.out.println("long: " + l);
	}

	@ConsoleCommand
	public void print (float f) {
		System.out.println("float: " + f);
	}

	@ConsoleCommand
	public void print (double d) {
		System.out.println("double: " + d);
	}

	@ConsoleCommand
	public void print (boolean b) {
		System.out.println("boolean: " + b);
	}

	@ConsoleCommand
	public void print (String s) {
		System.out.println("String: " + s);
	}

	@ConsoleCommand(
		successMessage = "Bob"
	)
	public int add (int x1, int x2) {
		return x1 + x2;
	}

	@ConsoleCommand
	public int getInt () {
		return 5;
	}

	@ConsoleCommand
	public Color setColor(Color color) {
		return null;
	}

}
