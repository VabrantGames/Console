
package com.vabrant.console.commandextension.parsers;

import com.vabrant.console.commandextension.ClassReference;

public class ClassReferenceParser implements Parsable<ParserContext, Object> {

	@Override
	public Object parse (ParserContext data) throws RuntimeException {
		ClassReference<?> instanceReference = data.getCache().getReference(data.getText());
		if (instanceReference == null) throw new RuntimeException("No instance found");
		return instanceReference.getReference();
	}
}
