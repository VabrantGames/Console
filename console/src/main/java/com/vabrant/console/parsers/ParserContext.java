
package com.vabrant.console.parsers;

import com.badlogic.gdx.utils.Array;
import com.vabrant.console.CommandExecutionData;
import com.vabrant.console.ConsoleCache;

public class ParserContext {

	private CommandExecutionData data;
	private String text;

	private Array<Object> args;

	public void setData(CommandExecutionData data) {
		this.data = data;
	}

	public CommandExecutionData getData() {
		return data;
	}

	public ParserContext setText (String text) {
		this.text = text;
		return this;
	}

	public String getText () {
		return text;
	}

	public ConsoleCache getCache () {
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
