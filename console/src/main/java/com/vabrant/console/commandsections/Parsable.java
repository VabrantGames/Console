package com.vabrant.console.commandsections;

import com.vabrant.console.ConsoleCache;

public interface Parsable<T> {
	public T parse(ConsoleCache cache, String text) throws RuntimeException;
}
