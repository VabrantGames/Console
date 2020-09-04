package com.vabrant.console.commandsections;

import com.badlogic.gdx.utils.Pool.Poolable;
import com.vabrant.console.commandsections.MethodArgument.MethodArgumentInfo;

public class CommandSection implements Poolable {

	private boolean hasBeenParsed;
	private Argument argumentType;
	private String text;
//	private Object argumentObject;
	private ContainerInfo containerInfo;
	private MethodArgumentInfo methodArgumentInfo;
	private Class<?> returnType;
	private Object returnObject;
	
	public void setMethodArgumentInfo(MethodArgumentInfo methodArgumentInfo) {
		this.methodArgumentInfo = methodArgumentInfo;
	}
	
	public MethodArgumentInfo getMethodArgumentInfo() {
		return methodArgumentInfo;
	}
	
	public void setContainerInfo(ContainerInfo argumentGroupInfo) {
		this.containerInfo = argumentGroupInfo;
	}
	
	public ContainerInfo getContainerInfo() {
		return containerInfo;
	}
	
	public void setReturnType(Class<?> returnType) {
		this.returnType = returnType;
	}
	
	public Class<?> getReturnType(){
		if(returnObject != null) return returnObject.getClass();
		if(returnType != null) return returnType;
		return null;
	}
	
	public void setReturnObject(Object returnObject) {
		this.returnObject = returnObject;
	}
	
	public Object getReturnObject() {
		return returnObject;
	}
	
	public void setHasBeenParsed(boolean hasBeenParsed) {
		this.hasBeenParsed = hasBeenParsed;
	}
	
	public boolean hasBeenParsed() {
		return hasBeenParsed;
	}
	
//	public void setArgumentObject(Object object) {
//		argumentObject = object;
//	}
	
//	public Object getArgumentObject() {
//		return argumentObject;
//	}

	public void setArgumentType(Argument argumentType) {
		this.argumentType = argumentType;
	}
	
	public Argument getArgumentType() {
		return argumentType;
	}
	
	public void setText(String text) {
		this.text = text;
	}
	
	public String getText() {
		return text;
	}

	@Override
	public void reset() {
		argumentType = null;
		text = null;
	}

}
