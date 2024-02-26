
package commandextension;

import com.vabrant.console.events.EventManager;
import com.vabrant.console.ConsoleData;
import com.vabrant.console.log.Log;
import com.vabrant.console.log.LogLevel;
import com.vabrant.console.log.LogManager;

@Deprecated
public class CommandData extends ConsoleData {

	public static final String FAIL_EVENT = "fail";
	public static final String SUCCESS_EVENT = "success";

	private CommandCache cache;
	private CommandExtensionResultEvent event;
	private EventManager eventManager;
	private LogManager logManager;

	public CommandData () {
		this(null);
	}

	public CommandData (LogManager logManager) {
		this.logManager = logManager;
// eventManager = new EventManager(FAIL_EVENT, SUCCESS_EVENT);
// event = new CommandEvent(this);
	}

	public void log (String message, LogLevel level) {
		log(null, message, level, false);
	}

	public void log (String tag, String message, LogLevel level) {
		log(tag, message, level, false);
	}

	public void log (String tag, String message, LogLevel level, boolean indent) {
		Log log = null;
		if (logManager != null) {
			log = logManager.create(tag, message, level);
			log.indent(indent);
			logManager.add(log);
		}
	}

// public void setCacheKeyMapReference (KeyMapReference reference) {
// cacheKeyMapReference = reference;
// }

	public CommandExtensionResultEvent getEvent () {
		return event;
	}

	public EventManager getEventManager () {
		return eventManager;
	}

	public void setConsoleCache (CommandCache cache) {
		this.cache = cache;
		event.clear();

// if (cacheKeyMapReference != null) {
// if (cache == null) {
// cacheKeyMapReference.setReference(null);
// } else {
// cacheKeyMapReference.setReference(cache.getKeyMap());
// }
// }
	}

	public CommandCache getConsoleCache () {
		return cache;
	}
}
