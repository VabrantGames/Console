
package commandextension;

import com.vabrant.console.*;
import com.vabrant.console.events.EventManager;
import com.vabrant.console.gui.GUIConsole;
import com.vabrant.console.log.LogManager;

public class CommandExtension extends ConsoleExtension {

	private static final String defaultName = "Command";

	private CommandCache cache;
	private CommandExtensionResultEvent event;
	private LogManager logManager;
	private CommandExtensionCore core;
	private EventManager eventManager;

	public CommandExtension () {
		this(defaultName);
	}

	public CommandExtension (String name) {
		super(name);
		event = new CommandExtensionResultEvent();
		core = new CommandExtensionCore(this);
		eventManager = new EventManager();
		logManager = new LogManager(100, eventManager);
	}

	public LogManager getLogManager () {
		return logManager;
	}

	public void setConsoleCache (CommandCache cache) {
		this.cache = cache;
	}

	public CommandCache getCache () {
		return cache;
	}

	public CommandExtensionResultEvent getEvent () {
		return event;
	}

	@Override
	public Boolean execute (Object o) throws Exception {
		return core.execute(o);
	}

	@Override
	protected void addedToConsole (Console console) {

		if (console instanceof GUIConsole) {
			// create com.vabrant.console.command.test.gui stuff
		}
	}

}
