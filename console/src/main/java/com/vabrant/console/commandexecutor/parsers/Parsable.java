
package com.vabrant.console.commandexecutor.parsers;

public interface Parsable<T, U> {
	U parse (T t) throws Exception;
}
