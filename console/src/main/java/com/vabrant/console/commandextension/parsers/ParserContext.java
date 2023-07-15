
package com.vabrant.console.commandextension.parsers;

import com.badlogic.gdx.utils.Array;
import com.vabrant.console.commandextension.CommandData;
import com.vabrant.console.commandextension.CommandCache;

public class ParserContext {

	private CommandData data;
	private String text;

	private Array<Object> args;

	public void setData (CommandData data) {
		this.data = data;
	}

	public CommandData getData () {
		return data;
	}

	public ParserContext setText (String text) {
		this.text = text;
		return this;
	}

	public String getText () {
		return text;
	}

	public CommandCache getCache () {
		return data.getConsoleCache();
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
