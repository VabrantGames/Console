package com.vabrant.console;

import com.vabrant.console.annotation.ConsoleMethod;

public class Guitar extends Instrument{

	public Guitar() {
		super("Guitar");
	}
	
	@ConsoleMethod
	private void boom() {
		System.out.println("Boom");
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
	public void add(int n1, int n2) {
		System.out.println("result" + (n1 + n2));
	}
}
