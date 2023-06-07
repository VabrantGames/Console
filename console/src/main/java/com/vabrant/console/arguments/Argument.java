
package com.vabrant.console.arguments;

public abstract class Argument {

	private ArgumentStrategy strategy;

	public Argument (ArgumentStrategy strategy) {
		this.strategy = strategy;
	}

	public <T> boolean isType (T t) {
		return strategy.isType(t);
	}

	public interface ArgumentStrategy<T> {
		boolean isType (T t);
	}
}
