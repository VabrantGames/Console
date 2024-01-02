package com.vabrant.console.events;

public abstract class TargetEventListener<T> implements EventListener<T> {

	private Object target;

	protected TargetEventListener (Object target) {
		setTarget(target);
	}

	public void setTarget (Object target) {
		this.target = target;
	}

	@Override
	public Object getTarget () {
		return target;
	}
}
