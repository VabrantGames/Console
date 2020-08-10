package com.vabrant.console;

import java.util.regex.Pattern;

public class SectionSpecifier {
	
	Class<?> specifiedSection;
	Pattern pattern;

	public Class<?> getSpecifiedSectionClass() {
		return specifiedSection;
	}
	
	public Pattern getPattern() {
		return pattern;
	}
	
	public static class Builder {
		
		public static class Rules {
			public static final int CHARACTER = 0x01;
			public static final int DIGIT = 0x02;
			public static final int CUSTOM = 0x04;
			public static final int EXPLICT = 0x08;
			public static final int ZERO_OR_MORE = 0X10;
			public static final int ONE_OR_MORE = 0X20;
			public static final int ONCE_OR_NONE = 0X40;
			public static final int OPTIONAL = 0x80;
		}

		private SectionSpecifier buildSpecifier = new SectionSpecifier();
		private StringBuilder strBuilder = new StringBuilder();
		
		public Builder specifiedSection(Class<?> c) {
			buildSpecifier.specifiedSection = c;
			return this;
		}

		public Builder or() {
			strBuilder.append('|');
			return this;
		}
		
		public Builder addRule(int rule) {
			addRule(rule, "");
			return this;
		}
		
		public Builder addRule(int rule, String s) {
			if((rule & Rules.EXPLICT) > 0) {
				strBuilder.append(s);
			}
			else {
				boolean isCustom = (rule & Rules.CUSTOM) > 0;
				boolean useCharacters = (rule & Rules.CHARACTER) > 0;
				boolean useDigits = (rule & Rules.DIGIT) > 0;
				
				if(!isCustom && !useCharacters && !useDigits) throw new RuntimeException("Rule must have type: Rules.CUSTOM, Rules.CHARACTER or Rules.DIGIT");
				
				strBuilder.append('[');
				
				if(isCustom) {
					strBuilder.append(s);
				}
				else {
					if(useCharacters && useDigits) {
						strBuilder.append("\\w");
					}
					else if(useCharacters) {
						strBuilder.append("a-zA-Z");
					}
					else if(useDigits) {
						strBuilder.append("\\d");
					}
				}
				
				strBuilder.append(']');
				
				if((rule & Rules.ZERO_OR_MORE) > 0) {
					strBuilder.append('*');
				}
				else if((rule & Rules.ONE_OR_MORE) > 0) {
					strBuilder.append('+');
				}
				else if((rule & Rules.ONCE_OR_NONE) > 0){
					strBuilder.append('?');
				}
			}
			
			return this;
		}

		public SectionSpecifier build() {
			if(strBuilder.length() == 0) throw new RuntimeException("No rules set.");
			if(buildSpecifier.getSpecifiedSectionClass() == null) throw new RuntimeException("Specified class not set.");
			buildSpecifier.pattern = Pattern.compile(strBuilder.toString());
			return buildSpecifier;
		}
	}
	
}
