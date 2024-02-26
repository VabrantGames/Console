package com.vabrant.console;

import com.vabrant.console.CommandEngine.CommandCache;
import com.vabrant.console.CommandEngine.CommandEngine;

public class DefaultConsoleConfiguration {

	protected String extensionIdentifier;
	protected String systemIdentifier;
	protected CommandEngine commandEngine;
	protected CommandCache globalCommandCache;

	public void setCommandEngine (CommandEngine engine) {
		commandEngine = engine;
	}

	public void setExtensionIdentifier (String id) {
		extensionIdentifier = id;
	}

	public void setSystemIdentifier (String id) {
		systemIdentifier = id;
	}

	public void setGlobalCommandCache (CommandCache cache) {
		globalCommandCache = cache;
	}

}
