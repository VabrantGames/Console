
package com.vabrant.console;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.ObjectSet;
import com.badlogic.gdx.utils.StringBuilder;

public class DebugLogger {

	private static boolean USE_SYS_OUT = false;
	private static boolean RESTRICT_OUTPUT = false;

	public static final int NONE = 0;
	public static final int ERROR = 1;
	public static final int INFO = 2;
	public static final int DEBUG = 3;

	private static final StringBuilder STRING_BUILDER = new StringBuilder(50);
	private static final ObjectSet<DebugLogger> SOLO_LOGGERS = new ObjectSet<>();

	public static void restrictOutput (boolean restrict) {
		RESTRICT_OUTPUT = restrict;
	}

	public static void useSysOut () {
		USE_SYS_OUT = true;
	}

	private boolean solo;
	private String className;
	private int level;

	public DebugLogger (Class<?> c) {
		this(c, NONE);
	}

	public DebugLogger (Class<?> c, int level) {
		className = c.getSimpleName();
		this.level = level;
	}

	public DebugLogger (String name, int level) {
		className = name;
		this.level = level;
	}

	public String getClassName () {
		return className;
	}

	public void setLevel (int level) {
		this.level = MathUtils.clamp(level, NONE, DEBUG);
	}

	public int getLevel() {
		return level;
	}

	public void info (String message) {
		info(message, null);
	}

	public void info (String message, String body) {
		if (RESTRICT_OUTPUT || level < INFO) return;
		print(message, body, null, INFO);
	}

	public void debug (String message) {
		debug(message, null);
	}

	public void debug (String message, String body) {
		if (RESTRICT_OUTPUT || level < DEBUG) return;
		print(message, body, null, DEBUG);
	}

	public void error (String message) {
		error(message, null);
	}

	public void error (String message, String body) {
		error(message, body, null);
	}

	public void error (String message, String body, Exception exception) {
		if (RESTRICT_OUTPUT || level < ERROR) return;
		print(message, body, exception, ERROR);
	}

	public void solo (boolean solo) {
		if (RESTRICT_OUTPUT) return;

		this.solo = solo;

		if (solo) {
			SOLO_LOGGERS.add(this);
		} else {
			SOLO_LOGGERS.remove(this);
		}
	}

	public void reset () {
		level = NONE;
		solo(false);
	}

	private void print (String message, String body, Exception exception, int level) {
		if (SOLO_LOGGERS.size > 0 && !solo) return;

		STRING_BUILDER.clear();

		STRING_BUILDER.append(message);

		if (body != null) {
			STRING_BUILDER.append(" : ");
			STRING_BUILDER.append(body);
		}

		if (USE_SYS_OUT) {
			STRING_BUILDER.insert(0, "[");
			STRING_BUILDER.insert(1, className);
			STRING_BUILDER.insert(1 + className.length(), "] ");
			System.out.println(STRING_BUILDER.toString());

			if (exception != null) {
				exception.printStackTrace(System.out);
			}
		} else {
			switch (level) {
			case ERROR:
				if (exception == null) {
					Gdx.app.error(className, STRING_BUILDER.toString());
				} else {
					Gdx.app.error(className, STRING_BUILDER.toString(), exception);
				}
				break;
			case INFO:
				Gdx.app.log(className, STRING_BUILDER.toString());
				break;
			case DEBUG:
				Gdx.app.debug(className, STRING_BUILDER.toString());
				break;
			}
		}
	}
}
