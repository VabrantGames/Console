package com.vabrant.console.arguments.strategies.pattern;

import java.util.regex.Pattern;

class PatternBuilder {

    static PatternBuilder INSTANCE = null;

    static PatternBuilder getInstance() {
        if (INSTANCE == null) INSTANCE = new PatternBuilder();
        return INSTANCE;
    }

    public static final int CHARACTER = 0x01;
    public static final int DIGIT = 0x02;
    public static final int CUSTOM = 0x04;
    public static final int EXPLICT = 0x08;
    public static final int ZERO_OR_MORE = 0X10;
    public static final int ONE_OR_MORE = 0X20;
    public static final int ONCE_OR_NONE = 0X40;
    public static final int OPTIONAL = 0x80;

    private StringBuilder strBuilder = new StringBuilder();

    PatternBuilder or() {
        strBuilder.append('|');
        return this;
    }

    PatternBuilder addRule(int rule) {
        addRule(rule, "");
        return this;
    }

    PatternBuilder addRule(int rule, String s) {
        if((rule & EXPLICT) > 0) {
            strBuilder.append(s);
        }
        else {
            boolean isCustom = (rule & CUSTOM) > 0;
            boolean useCharacters = (rule & CHARACTER) > 0;
            boolean useDigits = (rule & DIGIT) > 0;

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

            if((rule & ZERO_OR_MORE) > 0) {
                strBuilder.append('*');
            }
            else if((rule & ONE_OR_MORE) > 0) {
                strBuilder.append('+');
            }
            else if((rule & ONCE_OR_NONE) > 0){
                strBuilder.append('?');
            }
        }
        return this;
    }

    Pattern build() {
        return Pattern.compile(strBuilder.toString());
    }

}
