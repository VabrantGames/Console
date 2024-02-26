
package commandextension.parsers;

import com.badlogic.gdx.utils.Array;
import commandextension.CommandCache;
import commandextension.CommandExtension;

public class ParserContext {

	private String text;

	private Array<Object> args;
	private CommandExtension extension;

	public ParserContext (CommandExtension extension) {
		this.extension = extension;
	}

	public ParserContext setText (String text) {
		this.text = text;
		return this;
	}

	public String getText () {
		return text;
	}

	public CommandCache getCache () {
		return extension.getCache();
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
