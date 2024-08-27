
package com.vabrant.console;

import com.vabrant.console.CommandEngine.CommandCache;
import com.vabrant.console.CommandEngine.CommandExecutor;

public class DefaultConsoleConfiguration {

	protected String extensionIdentifier;
	protected String systemIdentifier;
	protected CommandExecutor commandExecutor;
	protected CommandCache globalCommandCache;
	public int logManagerMaxEntries = 100;

	public void setCommandEngine (CommandExecutor engine) {
		commandExecutor = engine;
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
