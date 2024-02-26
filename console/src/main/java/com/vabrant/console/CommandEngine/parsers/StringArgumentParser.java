
package com.vabrant.console.CommandEngine.parsers;

public class StringArgumentParser implements Parsable<ParserContext, String> {

	@Override
	public String parse (ParserContext data) throws RuntimeException {
		String str = data.getText();
		if (str.charAt(0) == '"') {
			return str.substring(1, str.length() - 1);
		} else {
			return str;
		}
	}
}
