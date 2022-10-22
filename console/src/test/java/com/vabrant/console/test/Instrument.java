package com.vabrant.console.test;

import com.vabrant.console.annotation.ConsoleMethod;

public class Instrument {

	public final String type;
	
	public Instrument(String type) {
		this.type = type;
	}
	
	@ConsoleMethod
	public void printType(Instrument instrument) {
		System.out.println("InstrumentType: " + instrument.type);
	}

	
	@ConsoleMethod
	public void printType() {
		System.out.println("InstrumentType: " + type);
	}
}
