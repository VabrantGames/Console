
package com.vabrant.console.parsers;

public class BooleanArgumentParser implements Parsable<ConsoleCacheAndStringInput, Boolean> {

	@Override
	public Boolean parse (ConsoleCacheAndStringInput data) {
		return Boolean.parseBoolean(data.getText());
	}

}
