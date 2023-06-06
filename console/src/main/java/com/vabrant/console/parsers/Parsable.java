package com.vabrant.console.parsers;

import com.vabrant.console.ConsoleCache;

public interface Parsable<T, U> {
	U parse(T t) throws Exception;
}
