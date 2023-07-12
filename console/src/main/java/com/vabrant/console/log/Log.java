
package com.vabrant.console.log;

import com.badlogic.gdx.utils.Pool;

public class Log implements Pool.Poolable {

	private String tag;
	private String message;
	private LogLevel level;

	public Log setTag (String tag) {
		this.tag = tag;
		return this;
	}

	public String getTag () {
		return tag;
	}

	public Log setMessage (String message) {
		this.message = message;
		return this;
	}

	public String getMessage () {
		return message;
	}

	public Log setLogLevel (LogLevel level) {
		this.level = level;
		return this;
	}

	public LogLevel getLogLevel () {
		return level;
	}

	public String toSimpleString () {
		return (tag == null ? "" : tag) + " " + message;
	}

	@Override
	public void reset () {
		tag = null;
		message = null;
		level = null;
	}
}
