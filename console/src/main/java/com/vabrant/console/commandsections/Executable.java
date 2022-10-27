package com.vabrant.console.commandsections;

public interface Executable<T, U> {
//	public Object execute(Object... executableInfo) throws RuntimeException;
	U execute(T t) throws Exception;
}
