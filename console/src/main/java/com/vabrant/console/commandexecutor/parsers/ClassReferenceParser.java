
package com.vabrant.console.commandexecutor.parsers;

import com.vabrant.console.commandexecutor.ClassReference;
import com.vabrant.console.ConsoleRuntimeException;

public class ClassReferenceParser implements Parsable<ParserContext, Object> {

	@Override
	public Object parse (ParserContext data) throws RuntimeException {
		ClassReference<?> instanceReference = data.getCache().getReference(data.getText());
		if (instanceReference == null) throw new ConsoleRuntimeException("No instance found: " + data.getText());
		return instanceReference.getReference();
	}
}
