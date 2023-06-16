
package com.vabrant.console.parsers;

import com.badlogic.gdx.utils.Array;
import com.vabrant.console.ConsoleCache;

public class ParserContext {

	private ConsoleCache cache;
	private String text;

	private Array<Object> args;

	public ParserContext setConsoleCache (ConsoleCache cache) {
		this.cache = cache;
		return this;
	}

	public ParserContext setText (String text) {
		this.text = text;
		return this;
	}

	public String getText () {
		return text;
	}

	public ParserContext setCache (ConsoleCache cache) {
		this.cache = cache;
		return this;
	}

	public ConsoleCache getCache () {
		return cache;
	}

	public ParserContext setArgs (Array<Object> args) {
		this.args = args;
		return this;
	}

	public Array<Object> getArgs () {
		return args;
	}

	public void clear () {
		text = null;
		args = null;
	}
}
