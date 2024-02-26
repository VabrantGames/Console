
package commandextension.arguments;

public class MethodArgument implements Argument {

	@Override
	public boolean isType (String s) {
		char c = s.charAt(0);
		return c == '.' || Character.isAlphabetic(c) && s.contains(".");
	}

}
