
package com.vabrant.console.parsers;

import com.vabrant.console.InstanceReference;

public class InstanceReferenceParser implements Parsable<ConsoleCacheAndStringInput, Object> {

	@Override
	public Object parse (ConsoleCacheAndStringInput data) throws RuntimeException {
		InstanceReference instanceReference = data.getCache().getInstanceReference(data.getText());
		if (instanceReference == null) throw new RuntimeException("No instance found");
		return instanceReference.getReference();
	}
}
