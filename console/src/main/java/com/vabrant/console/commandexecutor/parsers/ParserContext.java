
package com.vabrant.console.commandexecutor.parsers;

import com.badlogic.gdx.utils.Array;
import com.vabrant.console.commandexecutor.CommandCache;

public interface ParserContext {
	void setText (String text);

	String getText ();

	void setArgs (Array<Object> args);

	Array<Object> getArgs ();

	void setCache (CommandCache cache);

	CommandCache getCache ();

	void setGlobalCache (CommandCache cache);

	CommandCache getGlobalCache ();
}
