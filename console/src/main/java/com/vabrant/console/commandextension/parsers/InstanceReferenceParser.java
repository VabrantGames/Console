
package com.vabrant.console.commandextension.parsers;

import com.vabrant.console.commandextension.InstanceReference;

public class InstanceReferenceParser implements Parsable<ParserContext, Object> {

	@Override
	public Object parse (ParserContext data) throws RuntimeException {
		InstanceReference instanceReference = data.getCache().getInstanceReference(data.getText());
		if (instanceReference == null) throw new RuntimeException("No instance found");
		return instanceReference.getReference();
	}
}