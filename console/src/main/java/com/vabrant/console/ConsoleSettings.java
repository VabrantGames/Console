package com.vabrant.console;

public class ConsoleSettings {

	boolean checkSubclasses;
	boolean checkPrivateMethods;
	boolean caseSensitive;
	
	public ConsoleSettings() {}
	
	public ConsoleSettings(ConsoleSettings settings) {
		checkSubclasses = settings.checkSubclasses;
		checkPrivateMethods = settings.checkPrivateMethods;
		caseSensitive = settings.caseSensitive;
	}
	
	public ConsoleSettings checkSubclases(boolean checkSubclasses) {
		this.checkSubclasses = checkSubclasses;
		return this;
	}
	
	public ConsoleSettings checkPriveMethods(boolean checkPrivateMethods) {
		this.checkPrivateMethods = checkPrivateMethods;
		return this;
	}
	
	public ConsoleSettings caseSensitive(boolean caseSensitive) {
		this.caseSensitive = caseSensitive;
		return this;
	}
}
