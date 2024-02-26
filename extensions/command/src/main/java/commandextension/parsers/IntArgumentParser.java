
package commandextension.parsers;

public class IntArgumentParser implements Parsable<ParserContext, Integer> {

	@Override
	public Integer parse (ParserContext data) throws RuntimeException {
		int value = 0;
		String text = data.getText();

		if (text.startsWith("0b")) {
			value = Integer.parseInt(text.substring(2), 2);
		} else if (text.startsWith("0x")) {
			value = Integer.parseInt(text.substring(2), 16);
		} else if (text.startsWith("#")) {
			value = Integer.parseInt(text.substring(1), 16);
		} else {
			value = Integer.parseInt(text);
		}
		return value;
	}
}
