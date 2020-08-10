package com.vabrant.console.commandsections;

public interface Executable {
	public Object execute(Object... executableInfo) throws RuntimeException;
}
