
package com.vabrant.console;

public abstract class ConsoleData<T extends ConsoleStrategy<?>> {

	protected T strategy;

	final void setConsoleStrategy (T strategy) {
		this.strategy = strategy;
	}

	public T getConsoleStrategy () {
		return strategy;
	}

}
