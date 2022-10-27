package com.vabrant.console.arguments.strategies.pattern;

import java.util.regex.Matcher;

class PatternStrategyInput {
    private String text;
    private Matcher matcher;

    public void setText(String text) {
        this.text = text;
    }

    public void setMatcher(Matcher matcher) {
        this.matcher = matcher;
    }

    public String getText() {
        return text;
    }

   public Matcher getMatcher() {
        return matcher;
   }
}
