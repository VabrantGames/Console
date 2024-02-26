
package com.vabrant.console.CommandEngine.parsers;

public interface Parsable<T, U> {
	U parse (T t) throws Exception;
}
