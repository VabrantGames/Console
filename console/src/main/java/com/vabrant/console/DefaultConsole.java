
package com.vabrant.console;

import com.badlogic.gdx.utils.ObjectMap;

public class DefaultConsole implements Console {

	protected final ObjectMap<String, ConsoleStrategy<?>> strategies;
	protected DebugLogger logger;

	public DefaultConsole () {
		logger = new DebugLogger(this.getClass().getSimpleName(), DebugLogger.NONE);
		strategies = new ObjectMap<>();
	}

	public DebugLogger getLogger () {
		return logger;
	}

	@Override
	public void addStrategy (String name, ConsoleStrategy<?> strategy) {
		if (strategies.containsKey(name)) {
			throw new IllegalArgumentException("Strategy with name '" + name + "' already added");
		}

		strategies.put(name, strategy);
	}

	@Override
	public ConsoleStrategy getStrategy (String name) {
		return strategies.get(name);
	}

	@Override
	public final boolean execute (Object o) {
		if (o instanceof String) {
			String str = (String)o;
			if (!str.startsWith("/")) {
				logger.error("Input must start with '/'");
				return false;
			}

			int idx = str.indexOf(" ");

			if (idx == -1) return false;

			ConsoleStrategy<?> strat = strategies.get(str.substring(1, idx));

			if (strat == null) {
				logger.error("No strategy found for name '" + str.substring(1, idx) + "'");
				return false;
			}

			try {
				strat.execute(str.substring(idx + 1));
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}

			return true;
		}

		return false;
	}
}
