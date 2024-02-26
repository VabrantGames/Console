
package com.vabrant.console;

import com.badlogic.gdx.utils.ObjectMap;
import com.vabrant.console.CommandEngine.CommandCache;
import com.vabrant.console.CommandEngine.CommandEngine;
import com.vabrant.console.CommandEngine.DefaultCommandCache;
import com.vabrant.console.CommandEngine.DefaultCommandEngine;
import com.vabrant.console.events.ConsoleExtensionChangeEvent;
import com.vabrant.console.events.Event;
import com.vabrant.console.events.EventListener;
import com.vabrant.console.events.EventManager;
import com.vabrant.console.log.LogLevel;
import com.vabrant.console.log.LogManager;

import java.lang.reflect.Array;

public class DefaultConsole implements Console {

	protected static String extensionIdentifier;
	protected static String systemIdentifier;
	protected ConsoleExtension activeExtension;
	protected final ObjectMap<String, ConsoleExtension> extensions;
	protected DebugLogger logger;
	protected LogManager logManager;
	protected EventManager eventManager;
	protected ConsoleExtensionChangeEvent extensionChangeEvent;
	protected CommandCache globalCache;
	protected CommandEngine commandEngine;

	public DefaultConsole () {
		this(null);
	}

	public DefaultConsole (DefaultConsoleConfiguration config) {
		logger = new DebugLogger(this.getClass().getSimpleName(), DebugLogger.NONE);
		extensions = new ObjectMap<>();
		eventManager = new EventManager(ConsoleExtensionChangeEvent.class);
		extensionChangeEvent = new ConsoleExtensionChangeEvent();
		logManager = new LogManager(100, eventManager);

		if (config == null) {
			config = new DefaultConsoleConfiguration();
		}

		if (config.extensionIdentifier == null || config.extensionIdentifier.isEmpty()) {
			extensionIdentifier = "$";
		} else {
			extensionIdentifier = config.extensionIdentifier;
		}

		if (config.systemIdentifier == null || config.systemIdentifier.isEmpty()) {
			systemIdentifier = "/";
		} else {
			systemIdentifier = config.systemIdentifier;
		}

		if (config.globalCommandCache == null) {
			globalCache = new DefaultCommandCache();
		} else {
			globalCache = config.globalCommandCache;
		}

		if (config.commandEngine == null) {
			commandEngine = new DefaultCommandEngine(globalCache);
		} else {
			commandEngine = config.commandEngine;
		}

	}

	@Override
	public <T extends Event> void subscribeToEvent (Class<T> event, EventListener<T> listener) {
		eventManager.subscribe(event, listener);
	}

	@Override
	public <T extends Event> boolean unsubscribeFromEvent (Class<T> event, EventListener<T> listener) {
		return eventManager.unsubscribe(event, listener);
	}

	@Override
	public <T extends Event> void fireEvent (Class<T> type, T event) {
		eventManager.fire(type, event);
	}

	@Override
	public <T extends Event> void postFireEvent (Class<T> type, T event) {
		eventManager.postFire(type, event);
	}

	@Override
	public void setActiveExtension (ConsoleExtension extension) {
		if (extension != null && !extensions.containsValue(extension, false)) return;
		setActiveExtension0(extension);
	}

	private void setActiveExtension0 (ConsoleExtension extension) {
		activeExtension = extension;
		extensionChangeEvent.setConsoleExtension(activeExtension);
		eventManager.fire(ConsoleExtensionChangeEvent.class, extensionChangeEvent);
	}

	@Override
	public ConsoleExtension getActiveExtension () {
		return activeExtension;
	}

	@Override
	public EventManager getEventManager () {
		return eventManager;
	}

	public DebugLogger getLogger () {
		return logger;
	}

	@Override
	public LogManager getLogManager () {
		return logManager;
	}

	@Override
	public void setCommandEngine (CommandEngine engine) {
		this.commandEngine = engine;
	}

	@Override
	public CommandEngine getCommandEngine () {
		return commandEngine;
	}

	@Override
	public void addExtension (ConsoleExtension extension) {
		if (extension.getName() == null) throw new IllegalArgumentException("Extension name can't be null");

		if (extensions.containsKey(extension.getName())) {
			throw new IllegalArgumentException("Extension with name already exists: " + extension.getName());
		}

		extensions.put(extension.getName(), extension);
		extension.setConsole(this);
	}

	@Override
	public ConsoleExtension getExtension (String name) {
		return extensions.get(name);
	}

	@Override
	public final boolean execute (Object o) {
// if (o == null) return false;

		ConsoleExtension extension = activeExtension;
		Object input = o;

		try {
			if (o == null) throw new ConsoleRuntimeException("Input is null");

			if (o instanceof String) {
				String str = ((String)o).trim();

// try {
				if (str.isEmpty()) {
					throw new ConsoleRuntimeException("Input is empty");
				}

				if (str.startsWith(systemIdentifier)) {
					if (commandEngine == null) {
						throw new ConsoleRuntimeException("No CommandEngine set");
					}

					if (globalCache == null) {
						throw new ConsoleRuntimeException("No global CommandCache set");
					}

					commandEngine.execute(globalCache, str.substring(systemIdentifier.length()));
					return true;
				}
// } catch (Exception e) {
// // Create log
// logManager.add("Execution Failed", e.getMessage(), LogLevel.ERROR);
// return false;
// }

				if (str.startsWith(extensionIdentifier)) {
					if (str.length() == extensionIdentifier.length()) throw new ConsoleRuntimeException("Missing Command");

					int idx = str.indexOf(" ");

					// Set the active extension. No spaces
					// Example:
					// /ext
					if (idx == -1) {
						extension = extensions.get(str.substring(extensionIdentifier.length()));

						if (extension != null) {
							setActiveExtension0(extension);
							return true;
						} else {
							throw new ConsoleRuntimeException("No extension found: " + str.substring(extensionIdentifier.length()));
// return false;
						}
					}

					// Index of the first space can't be the last character
// if (idx == str.length() - 1)
// return false;

					// Send input to specified extension. Has spaces
					// Example:
					// /ext input1 input2
					extension = extensions.get(str.substring(extensionIdentifier.length(), idx));

					if (extension == null)
						throw new ConsoleRuntimeException("No extension found: " + str.substring(extensionIdentifier.length(), idx));

					// No extension found
// return false;

					input = str.substring(idx + 1);
				}
			} else if (o instanceof Object[]) {
				Object[] arr = (Object[])o;

				if (arr.length == 0) throw new ConsoleRuntimeException("Array is empty");

				if (!(arr[0] instanceof String)) throw new ConsoleRuntimeException("First argument must be a string");

// if (arr[0] instanceof String) {
				String str = ((String)arr[0]).trim();

				if (str.startsWith(extensionIdentifier)) {
					extension = extensions.get(str.substring(1));

					// No extension found
					if (extension == null) return false;

					// No other arguments
					if (arr.length == 1) {
						setActiveExtension0(extension);
						return true;
					}

					Object[] dst = (Object[])Array.newInstance(arr.getClass().getComponentType(), arr.length - 1);

					System.arraycopy(arr, 1, dst, 0, arr.length - 1);
					input = dst;
				}
// }

			} else if (o instanceof ConsoleExtensionExecutable) {
				ConsoleExtensionExecutable e = (ConsoleExtensionExecutable)o;
				extension = e.getConsoleExtension();
				input = e.getArguments();

				if (input == null) return false;
			}

			if (extension == null) {
				// Create log
				return false;
			}
		} catch (Exception e) {
			logManager.add("Execution Failed", e.getMessage(), LogLevel.ERROR);
			return false;
		}

		return execute(extension, input);
	}

	@Override
	public boolean execute (ConsoleExtension extension, Object input) {
		try {
			return extension.execute(input);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
}
