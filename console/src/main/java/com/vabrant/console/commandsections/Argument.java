package com.vabrant.console.commandsections;

import com.badlogic.gdx.utils.Pool.Poolable;
import com.vabrant.console.SectionType;

public interface Argument<T> extends Poolable, SectionType {
//	public void setSection(CommandSection section);
//	public void setArgument(T argument);
	public Class<T> getArgumentType();
	public T getArgument();
}
