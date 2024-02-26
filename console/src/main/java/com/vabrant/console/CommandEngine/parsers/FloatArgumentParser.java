
package com.vabrant.console.CommandEngine.parsers;

public class FloatArgumentParser implements Parsable<ParserContext, Float> {

	@Override
	public Float parse (ParserContext data) throws RuntimeException {
		return Float.parseFloat(data.getText());
	}
}
