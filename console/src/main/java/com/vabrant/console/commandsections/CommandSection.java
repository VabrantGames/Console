package com.vabrant.console.commandsections;

import com.badlogic.gdx.utils.Pool.Poolable;

public class CommandSection implements Poolable {
	
	private Argument argument;
	private String text;
	private Object argumentObject;
	private ArgumentGroupInfo argumentGroupInfo;
	
	public void setArgumentGroupInfo(ArgumentGroupInfo argumentGroupInfo) {
		this.argumentGroupInfo = argumentGroupInfo;
	}
	
	public ArgumentGroupInfo getArgumentGroupInfo() {
		return argumentGroupInfo;
	}
	
	public void setArgumentObject(Object object) {
		argumentObject = object;
	}
	
	public Object getArgumentObject() {
		return argumentObject;
	}

	public void setArgument(Argument argument) {
		this.argument = argument;
	}
	
	public Argument getArgumentType() {
		return argument;
	}
	
	public void setText(String text) {
		this.text = text;
	}
	
	public String getText() {
		return text;
	}

	@Override
	public void reset() {
		argument = null;
		text = null;
	}

}
