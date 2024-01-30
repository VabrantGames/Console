
package com.vabrant.console;

import com.badlogic.gdx.utils.ObjectMap;
import com.vabrant.console.events.ConsoleExtensionChangeEvent;
import com.vabrant.console.events.Event;
import com.vabrant.console.events.EventListener;
import com.vabrant.console.events.EventManager;
import com.vabrant.console.log.LogManager;

import java.lang.reflect.Array;

public class DefaultConsole implements Console {

	protected ConsoleExtension activeExtension;
	protected final ObjectMap<String, ConsoleExtension> extensions;
	protected DebugLogger logger;
	protected LogManager logManager;
	protected EventManager eventManager;
	protected ConsoleExtensionChangeEvent extensionChangeEvent;

	public DefaultConsole () {
		logger = new DebugLogger(this.getClass().getSimpleName(), DebugLogger.NONE);
		extensions = new ObjectMap<>();
		eventManager = new EventManager(ConsoleExtensionChangeEvent.class);
		extensionChangeEvent = new ConsoleExtensionChangeEvent();
		logManager = new LogManager(100, eventManager);
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
	public void addExtension (ConsoleExtension extension) {
		if (extension.getName() == null) throw new IllegalArgumentException("Extension name can't be null");

		if (extensions.containsKey(extension.getName())) {
			throw new IllegalArgumentException("Strategy with name '" + extension.getName() + "' already added");
		}

		extensions.put(extension.getName(), extension);
	}

	@Override
	public ConsoleExtension getExtension (String name) {
		return extensions.get(name);
	}

	@Override
	public final boolean execute (Object o) {
		if (o == null) return false;

		ConsoleExtension extension = activeExtension;
		Object input = o;

		if (o instanceof String) {
			String str = ((String)o).trim();

			if (str.startsWith("/")) {
				if (str.length() == 1) return false;

				int idx = str.indexOf(" ");

				// Set the active extension. No spaces
				// Example:
				// /ext
				if (idx == -1) {
					extension = extensions.get(str.substring(1));

					if (extension != null) {
						setActiveExtension0(extension);
						return true;
					} else {
						return false;
					}
				}

				// Index of the first space can't be the last character
				if (idx == str.length() - 1) return false;

				// Send input to specified extension. Has spaces
				// Example:
				// /ext input1 input2
				extension = extensions.get(str.substring(1, idx));

				if (extension == null) {
					// No extension found
					return false;
				}

				input = str.substring(idx + 1);
			}

		} else if (o instanceof Object[]) {
			Object[] arr = (Object[])o;

			if (arr.length == 0) return false;

			if (arr[0] instanceof String) {
				String str = (String)arr[0];

				if (str.length() > 1 && str.startsWith("/")) {
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
			}

		} else if (o instanceof ConsoleExtensionExecutable) {
			ConsoleExtensionExecutable e = (ConsoleExtensionExecutable)o;
			extension = e.getConsoleExtension();
			input = e.getArgument();

			if (input == null) return false;
		}

		if (extension == null) {
			// Create log
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
