
package com.vabrant.console.CommandEngine.parsers;

import com.badlogic.gdx.utils.Array;
import com.vabrant.console.CommandEngine.CommandCache;

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
