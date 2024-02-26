
package commandextension.arguments;

public class IntArgument implements Argument {

	@Override
	public boolean isType (String s) {
		return Character.isDigit(s.charAt(0)) && !s.contains(".") && Character.isDigit(s.charAt(s.length() - 1));
	}
}
