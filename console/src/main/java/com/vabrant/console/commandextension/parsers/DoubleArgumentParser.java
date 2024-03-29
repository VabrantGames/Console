
package com.vabrant.console.commandextension.parsers;

public class DoubleArgumentParser implements Parsable<ParserContext, Double> {
	@Override
	public Double parse (ParserContext data) throws RuntimeException {
		String text = data.getText();
		return Double.parseDouble(text.substring(0, text.length() - 1));
	}
}
