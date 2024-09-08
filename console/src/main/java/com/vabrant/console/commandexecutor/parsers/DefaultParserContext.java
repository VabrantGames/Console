
package com.vabrant.console.commandexecutor.parsers;

import com.badlogic.gdx.utils.Array;
import com.vabrant.console.commandexecutor.CommandCache;

public class DefaultParserContext implements ParserContext {

	private String text;
	private Array<Object> args;
	private CommandCache cache;
	private CommandCache globalCache;

	public DefaultParserContext () {
		this(null);
	}

	public DefaultParserContext (CommandCache globalCache) {
		setGlobalCache(globalCache);
	}

	@Override
	public void setText (String text) {
		this.text = text;
	}

	@Override
	public String getText () {
		return text;
	}

	@Override
	public void setArgs (Array<Object> args) {
		this.args = args;
	}

	@Override
	public Array<Object> getArgs () {
		return args;
	}

	@Override
	public void setCache (CommandCache cache) {
		this.cache = cache;
	}

	@Override
	public CommandCache getCache () {
		return cache;
	}

	@Override
	public void setGlobalCache (CommandCache globalCache) {
		this.globalCache = globalCache;
	}

	@Override
	public CommandCache getGlobalCache () {
		return globalCache;
	}
}
