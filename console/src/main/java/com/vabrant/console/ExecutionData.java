
package com.vabrant.console;

public abstract class ExecutionData<T extends ExecutionStrategy<?>> {

	protected T strategy;

	final void setExecutionStrategy (T strategy) {
		this.strategy = strategy;
	}

	public T getExecutionStrategy () {
		return strategy;
	}

// void setExecutionStrategy(T T)
}
