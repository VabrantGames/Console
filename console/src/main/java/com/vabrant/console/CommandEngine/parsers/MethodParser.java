
package com.vabrant.console.CommandEngine.parsers;

import com.vabrant.console.CommandEngine.MethodCommand;

public class MethodParser implements Parsable<ParserContext, MethodCommand> {

	@Override
	public MethodCommand parse (ParserContext context) {
		return null;
// String text = context.getText();
// CommandCache cache = context.getCache();
// Array<Object> args = context.getArgs();
// ObjectSet<Command> methods = null;
// String referenceName = null;
// String methodName = null;
//
// // Get methods by name or reference
// if (text.charAt(0) == '.') {
// methodName = text.substring(1);
// methods = cache.getAllCommandsWithName(methodName);
//
// if (methods == null) {
// throw new RuntimeException("No methods with name '" + methodName + "' found");
// }
// } else if (Character.isAlphabetic(text.charAt(0))) {
// int sepIdx = text.indexOf('.');
// if (sepIdx != -1) {
// referenceName = text.substring(0, sepIdx);
//
// ClassReference<?> reference = cache.getReference(referenceName);
//
// if (reference == null) {
// throw new RuntimeException("Reference '" + referenceName + "' not found");
// }
//
// methodName = text.substring(sepIdx + 1);
//
// if (!cache.hasCommandWithName(reference, methodName)) {
// throw new RuntimeException("Method '" + methodName + "' not found for reference '" + referenceName + "'");
// }
//
// methods = cache.getAllCommandsForReference(reference);
//
// if (methods == null) {
// throw new RuntimeException(
// "No methods with name '" + methodName + "' found for reference '" + referenceName + "'");
// }
// } else {
// methodName = text;
// methods = cache.getAllCommandsWithName(methodName);
//
// if (methods == null) {
// throw new RuntimeException("No methods with name '" + methodName + "' found");
// }
// }
// }
//
// // Match args
// final int argsSize = args.size;
// Class[] argTypes = argsSize == 0 ? CommandUtils.EMPTY_ARGUMENT_TYPES : new Class[argsSize];
// if (argsSize > 0) {
// for (int i = 0, len = argsSize, idx = len - 1; i < len; i++, idx--) {
// Object o = args.get(idx);
//
// if (o instanceof MethodContainer) {
// argTypes[i] = ((MethodContainer)o).getCommand().getReturnType();
// } else {
// argTypes[i] = o.getClass();
// }
// }
// }
//
// for (Command c : methods) {
// if (!c.getMethodName().equals(methodName)) continue;
// if (CommandUtils.areArgsEqual(c.getArgs(), argTypes)) {
// return c;
// }
// }
//
// // No method found
// StringBuilder builder = new StringBuilder(50);
// builder.append("Method '");
// builder.append(methodName);
// builder.append("' not found");
//
// if (referenceName != null) {
// builder.append(" for reference '");
// builder.append(referenceName);
// builder.append('\'');
// }
//
// builder.append(" with args '(");
// for (int i = 0; i < argTypes.length; i++) {
// builder.append(argTypes[i].getSimpleName());
//
// if (i < (argTypes.length - 1)) {
// builder.append(", ");
// }
// }
// builder.append(")'");
// throw new RuntimeException(builder.toString());
	}
}
