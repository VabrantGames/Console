
package com.vabrant.console.log;

import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.StringBuilder;
import com.badlogic.gdx.utils.TimeUtils;

public class Log implements Pool.Poolable {

	private boolean indent;
	private String tag;
	private String message;
	private LogLevel level;
	private long timeStamp;

	public void stampTime() {
		timeStamp = TimeUtils.millis();
	}

	public Log indent(boolean indent) {
		this.indent = indent;
		return this;
	}

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

	public String toSimpleString() {
		return toSimpleString(new StringBuilder());
	}

	public String toSimpleString (StringBuilder builder) {
		if (builder.length() != 0) builder.clear();

		builder.append(" ");
		if (indent) {
			builder.append("    ");
		}

		if (tag != null) {
			builder.append(tag);
			builder.append(" : ");
		}

		builder.append(message);
		return builder.toString();
	}

	@Override
	public void reset () {
		tag = null;
		message = null;
		level = null;
		indent = false;
		timeStamp = 0;
	}
}
