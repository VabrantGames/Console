package com.vabrant.console;

import java.util.regex.Pattern;

public class SectionSpecifier {
	
	//Gets put into its own section. [...][Specifier][...]
	boolean ownSection;
	CharSequence specifier;
	Class<?> specifiedSection;
	Pattern pattern;

	public Class<?> getSpecifiedSectionClass() {
		return specifiedSection;
	}
	
	public CharSequence getSpecifier() {
		return specifier;
	}
	
	public boolean ownSection() {
		return ownSection;
	}
	
	public Pattern getPattern() {
		return pattern;
	}
	
	public static class Builder {
		
		public enum Rule {
			SINGLE_CHARACTER,
			ANY_CHARACTER,
			ANY_DIGIT,
			CHARACTER_OR_DIGIT,
			SPECIFIER,
			OR
		}
		
		public enum Quantifiers {
			NONE,
			ZERO_OR_MORE,
			ONE_OR_MORE,
			ONCE
		}

		private SectionSpecifier buildSpecifier = new SectionSpecifier();
		private StringBuilder strBuilder = new StringBuilder();
		
		public Builder specifiedSection(Class<?> c) {
			buildSpecifier.specifiedSection = c;
			return this;
		}
		
		public Builder ccc(char... c) {
			return this;
		}
		
		public Builder specifier(CharSequence c) {
			if(c.length() == 0 || c.length() > 2) throw new IllegalArgumentException("Specifier must be one or two characters.");
			buildSpecifier.specifier = c;
			return this;
		}
		
		public Builder terminatingSpecifiers(Class<?>... c) {
			return this;
		}
		
		public Builder setRule(Rule type) {
			setRule(type, Quantifiers.NONE);
			return this;
		}
		
		public Builder setRule(Rule type, Quantifiers quantifiers) {
			switch(type) {
				case ANY_CHARACTER:
					strBuilder.append("[a-zA-z]");
					break;
				case ANY_DIGIT:
					strBuilder.append("\\d");
					break;
				case CHARACTER_OR_DIGIT:
					strBuilder.append("\\w");
					break;
				case SPECIFIER:
					if(buildSpecifier.specifier == null) throw new IllegalArgumentException("Specifier needs to be set before rules.");
					strBuilder.append('[');
					strBuilder.append(buildSpecifier.specifier);
					strBuilder.append(']');
					break;
				case OR:
					strBuilder.append('|');
					return this;
			}
			setQuantifiers(quantifiers);
			return this;
		}

		private void setQuantifiers(Quantifiers quantifiers) {
			switch(quantifiers) {
				case ONCE:
					strBuilder.append('?');
					break;
				case ONE_OR_MORE:
					strBuilder.append('+');
					break;
				case ZERO_OR_MORE:
					strBuilder.append('*');
					break;
			}
		}

		public SectionSpecifier build() {
			if(strBuilder.length() == 0) throw new RuntimeException("No rules set.");
			buildSpecifier.pattern = Pattern.compile(strBuilder.toString());
			return buildSpecifier;
		}
	}
	
}
