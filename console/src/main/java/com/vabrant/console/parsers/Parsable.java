package com.vabrant.console.parsers;

import com.vabrant.console.ConsoleCache;

public interface Parsable<T, U> {
//	public T parse(ConsoleCache cache, String text) throws RuntimeException;
	public U parse(T t) throws RuntimeException;
}
