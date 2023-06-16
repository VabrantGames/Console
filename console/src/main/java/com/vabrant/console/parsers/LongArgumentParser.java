
package com.vabrant.console.parsers;

public class LongArgumentParser implements Parsable<ParserContext, Long> {

	@Override
	public Long parse (ParserContext data) throws RuntimeException {
		String text = data.getText();
		return Long.parseLong(text.substring(0, text.length() - 1));
	}
}
