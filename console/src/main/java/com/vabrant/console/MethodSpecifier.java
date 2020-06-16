package com.vabrant.console;

import com.vabrant.console.SectionSpecifier.Builder;
import com.vabrant.console.commandsections.MethodSection;

public class MethodSpecifier {
	
	public static SectionSpecifier create() {
		return new SectionSpecifier.Builder()
				.specifiedSection(MethodSection.class)
				.specifier(".")
				.setRule(Builder.Rule.ANY_CHARACTER)
				.setRule(Builder.Rule.CHARACTER_OR_DIGIT, Builder.Quantifiers.ZERO_OR_MORE)
				.setRule(Builder.Rule.SPECIFIER)
				.setRule(Builder.Rule.ANY_CHARACTER)
				.setRule(Builder.Rule.CHARACTER_OR_DIGIT, Builder.Quantifiers.ZERO_OR_MORE)
				.setRule(Builder.Rule.OR)
				.setRule(Builder.Rule.SPECIFIER)
				.setRule(Builder.Rule.ANY_CHARACTER)
				.setRule(Builder.Rule.CHARACTER_OR_DIGIT, Builder.Quantifiers.ZERO_OR_MORE)
				.build();
	}
	
	public static SectionSpecifier bob() {
		return new SectionSpecifier.Builder()
				.specifier("-tmp")
				.build();
	}

}
