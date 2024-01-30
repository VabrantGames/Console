
package com.vabrant.console.events;

import com.vabrant.console.ConsoleExtension;

public class ConsoleExtensionChangeEvent extends DefaultEvent {

	private ConsoleExtension extension;

	public void setConsoleExtension (ConsoleExtension extension) {
		this.extension = extension;
	}

	public ConsoleExtension getExtension () {
		return extension;
	}

}
