
package com.vabrant.console;

public interface Console {

	void addStrategy (String name, ConsoleStrategy<?> strategy);

	ConsoleStrategy getStrategy (String name);

	DebugLogger getLogger ();

	boolean execute (Object o);
}
