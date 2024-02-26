package com.vabrant.console.CommandEngine.parsers;

import com.vabrant.console.CommandEngine.ClassReference;
import com.vabrant.console.ConsoleRuntimeException;

public class GlobalClassReferenceParser implements Parsable<ParserContext, Object> {
	@Override
	public Object parse (ParserContext data) throws Exception {
		if (data.getGlobalCache() == null) return null;
		String name = data.getText().substring(1);
		ClassReference<?> ref = data.getGlobalCache().getReference(name);
		if (ref == null) throw new ConsoleRuntimeException("No global reference found: " + name);
		return ref.getReference();
	}
}
