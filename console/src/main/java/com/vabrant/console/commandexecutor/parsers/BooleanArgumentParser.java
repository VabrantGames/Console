
package com.vabrant.console.commandexecutor.parsers;

public class BooleanArgumentParser implements Parsable<ParserContext, Boolean> {

	@Override
	public Boolean parse (ParserContext data) {
		return Boolean.parseBoolean(data.getText());
	}

}
