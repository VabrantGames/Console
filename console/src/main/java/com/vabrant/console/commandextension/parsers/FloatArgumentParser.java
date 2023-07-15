
package com.vabrant.console.commandextension.parsers;

public class FloatArgumentParser implements Parsable<ParserContext, Float> {

	@Override
	public Float parse (ParserContext data) throws RuntimeException {
		return Float.parseFloat(data.getText());
	}
}
