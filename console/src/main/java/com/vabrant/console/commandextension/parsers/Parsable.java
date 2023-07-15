
package com.vabrant.console.commandextension.parsers;

public interface Parsable<T, U> {
	U parse (T t) throws Exception;
}
