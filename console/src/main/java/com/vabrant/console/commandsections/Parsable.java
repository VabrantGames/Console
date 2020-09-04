package com.vabrant.console.commandsections;

import com.vabrant.console.ConsoleCache;

public interface Parsable<T> {
	public <S> T parse(ConsoleCache cache, String text, S extra) throws RuntimeException;
}
