
package com.vabrant.console;

import com.badlogic.gdx.utils.ObjectMap;
import com.vabrant.console.CommandEngine.CommandCache;
import com.vabrant.console.CommandEngine.CommandExecutor;
import com.vabrant.console.CommandEngine.DefaultCommandCache;
import com.vabrant.console.CommandEngine.DefaultCommandExecutor;
import com.vabrant.console.events.ConsoleExtensionChangeEvent;
import com.vabrant.console.events.Event;
import com.vabrant.console.events.EventListener;
import com.vabrant.console.events.EventManager;
import com.vabrant.console.log.LogLevel;
import com.vabrant.console.log.LogManager;

import java.lang.reflect.Array;

public class DefaultConsole implements Console {

	protected boolean printStackTrace;
	protected static String extensionIdentifier;
	protected static String systemIdentifier;
	protected ConsoleExtension activeExtension;
	protected final ObjectMap<String, ConsoleExtension> extensions;
	protected DebugLogger logger;
	protected LogManager logManager;
	protected EventManager eventManager;
	protected ConsoleExtensionChangeEvent extensionChangeEvent;
	protected CommandCache globalCache;
	protected CommandExecutor commandExecutor;

	public DefaultConsole () {
		this(null);
	}

	public DefaultConsole (DefaultConsoleConfiguration config) {
		logger = new DebugLogger(this.getClass().getSimpleName(), DebugLogger.NONE);
		extensions = new ObjectMap<>();
		eventManager = new EventManager(ConsoleExtensionChangeEvent.class);
		extensionChangeEvent = new ConsoleExtensionChangeEvent();

		if (config == null) {
			config = new DefaultConsoleConfiguration();
		}

		logManager = new LogManager(config.logManagerMaxEntries, eventManager);

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

		if (config.commandExecutor == null) {
			commandExecutor = new DefaultCommandExecutor(logManager, globalCache);
		} else {
			commandExecutor = config.commandExecutor;
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

	public void printStackTrace (boolean printStackTrace) {
		this.printStackTrace = printStackTrace;
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
	public void setCommandEngine (CommandExecutor engine) {
		this.commandExecutor = engine;
	}

	@Override
	public CommandExecutor getCommandExecutor () {
		return commandExecutor;
	}

	public CommandCache getGlobalCache () {
		return globalCache;
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
		ConsoleExtension extension = activeExtension;
		Object input = o;

		try {
			if (o == null) throw new ConsoleRuntimeException("Input is null");

			if (o instanceof String) {
				String str = ((String)o).trim();

				if (str.isEmpty()) {
					throw new ConsoleRuntimeException("Input is empty");
				}

				if (str.startsWith(systemIdentifier)) {
					if (commandExecutor == null) {
						throw new ConsoleRuntimeException("No CommandExecutor set");
					}

					if (globalCache == null) {
						throw new ConsoleRuntimeException("No global CommandCache set");
					}

					return commandExecutor.execute(globalCache, str.substring(systemIdentifier.length())).getExecutionStatus();
				} else if (str.startsWith(extensionIdentifier)) {
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
						}
					}

					// Send input to specified extension. Has spaces
					// Example:
					// /ext input1 input2
					extension = extensions.get(str.substring(extensionIdentifier.length(), idx));

					if (extension == null)
						throw new ConsoleRuntimeException("No extension found: " + str.substring(extensionIdentifier.length(), idx));

					input = str.substring(idx + 1);
				} else if (activeExtension == null) {
					return commandExecutor.execute(globalCache, str).getExecutionStatus();
				}
			} else if (o instanceof Object[]) {
				Object[] arr = (Object[])o;

				if (arr.length == 0) throw new ConsoleRuntimeException("Array is empty");

				if (arr[0] instanceof String) {
					String str = (String)arr[0];

					if (str.length() > 1 && str.startsWith(extensionIdentifier)) {
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
				} else if (arr[0] instanceof ConsoleExtension) {
					extension = (ConsoleExtension)arr[0];

					if (arr.length == 1) {
						setActiveExtension(extension);
						return true;
					}

					Object[] dst = (Object[])Array.newInstance(arr.getClass().getComponentType(), arr.length - 1);
					System.arraycopy(arr, 1, dst, 0, arr.length - 1);
					input = dst;
				}
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
			if (printStackTrace) e.printStackTrace();
			return false;
		}

		return execute(extension, input);
	}

	@Override
	public boolean execute (ConsoleExtension extension, Object input) {
		try {
			return extension.execute(input);
		} catch (Exception e) {
			logManager.add("Execution Failed", e.getMessage(), LogLevel.ERROR);
			if (printStackTrace) e.printStackTrace();
			return false;
		}
	}

}
