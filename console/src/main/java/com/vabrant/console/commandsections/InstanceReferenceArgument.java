package com.vabrant.console.commandsections;

import com.vabrant.console.ClassReference;
import com.vabrant.console.ConsoleCache;
import com.vabrant.console.SectionSpecifier;
import com.vabrant.console.SectionSpecifier.Builder.Rules;

public class InstanceReferenceArgument implements Argument, Parsable<Object> {
	
	public static SectionSpecifier createSpecifier() {
		return new SectionSpecifier.Builder()
				.specifiedSection(InstanceReferenceArgument.class)
				.addRule(Rules.CHARACTER)
				.addRule(Rules.CHARACTER | Rules.DIGIT | Rules.ZERO_OR_MORE)
				.build();
	}
	
	@Override
	public Object parse(ConsoleCache cache, String sectionText) throws RuntimeException {
		ClassReference reference = cache.getReference(sectionText);
		if(reference == null) throw new RuntimeException("Reference not found: " + sectionText);
		return reference.getReference();
	}

}
