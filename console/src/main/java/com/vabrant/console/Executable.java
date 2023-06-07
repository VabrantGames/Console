
package com.vabrant.console;

public interface Executable<T, U> {
	U execute (T t) throws Exception;
}
