package com.vabrant.console;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.StringBuilder;

public class DebugLogger {

private static boolean isRelease = false;
	
	public static final int NONE = 0;
	public static final int ERROR = 1;
	public static final int INFO = 2;
	public static final int DEBUG = 3;
	static final int DEVELOPMENT_DEBUG = 4;
	
	private static final StringBuilder STRING_BUILDER;	
	private static DebugLogger soloLogger;
	
	static {
		if(!isRelease) {
			STRING_BUILDER = new StringBuilder(50);
		}
		else {
			STRING_BUILDER = null;
		}
	}
	
	public static DebugLogger getLogger(Class<?> c, int level) {
		return isRelease ? null : new DebugLogger(c, level);
	}
	
	public static void setReleaseMode(boolean releaseMode) {
		DebugLogger.isRelease = releaseMode;
	}
	
	public static void solo(DebugLogger logger) {
		if(logger == null) return;
		soloLogger = logger;
	}
	
	private static final String EMPTY_STRING = ""; 
	
	private String className;
	private int level;
	
	private DebugLogger(Class<?> c) {
		this(c, NONE);
	}
	
	private DebugLogger(Class<?> c, int level) {
		if(!isRelease) {
			className = c.getSimpleName();
			this.level = level;
		}
	}
	
	public String getClassName() {
		return className;
	}
	
	public void setLevel(int level) {
		this.level = MathUtils.clamp(level, NONE, DEBUG);
	}
	
	public void info(String message) {
		info(message, null);
	}
	
	public void info(String message, String body) {
		if(isRelease || level < INFO) return;
		print(message, body, INFO);
	}
	
	public void debug(String message) {
		debug(message, null);
	}
	
	public void debug(String message, String body) {
		if(isRelease || level < DEBUG) return;
		print(message, body, DEBUG);
	}
	
	public void devDebug(String message) {
		devDebug(message, null);
	}
	
	public void devDebug(String message, String body) {
		if(level < DEVELOPMENT_DEBUG) return;
		print(message, body, DEVELOPMENT_DEBUG);
	}
	
	public void error(String message) {
		error(message, null);
	}
	
	public void error(String message, String body) {
		if(isRelease || level < ERROR) return;
		print(message, body, ERROR);
	}
	
	public void solo() {
		if(isRelease) return;
		DebugLogger.solo(this);
	}
	
	private void print(String message, String body, int level) {
		if(soloLogger != null && !soloLogger.equals(this)) return;
		
		STRING_BUILDER.clear();
		STRING_BUILDER.append(message);
		if(body != null) {
			STRING_BUILDER.append(" : ");
			STRING_BUILDER.append(body);
		}
		
		switch(level) {
			case ERROR:
				Gdx.app.error(className, STRING_BUILDER.toString());
				break;
			case INFO:
				Gdx.app.log(className, STRING_BUILDER.toString());
				break;
			case DEBUG:
				Gdx.app.debug(className, STRING_BUILDER.toString());
				break;
			case DEVELOPMENT_DEBUG:
				Gdx.app.debug(className, STRING_BUILDER.toString());
				break;
		}
	}
}
