package com.vabrant.console;

import java.util.regex.Pattern;

import com.vabrant.console.SectionSpecifier.Builder.Rule;

public class ActionSpecifier {
	
	private static char ACTION_SPECIFIER_SPECIFIER = '-';
	
	CharSequence specifier;
	Pattern pattern;
	Class<?> specifiedSection;
	
	public ActionSpecifier create() {
		return this;
	}
	
	public ActionSpecifier setRule(Rule rule) {
		return this;
	}
	
	public static class TemporaryValueSpecifier extends ActionSpecifier{

		public TemporaryValueSpecifier() {
			create()
			.setRule(null)
			.setRule(null);
		}
	}
	
}
