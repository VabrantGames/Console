package com.vabrant.console.arguments.strategies.pattern;

import com.vabrant.console.SectionSpecifier;
import com.vabrant.console.arguments.Argument;
import com.vabrant.console.arguments.LongArgument;

import java.util.regex.Matcher;

public class PatternLongArgumentStrategy implements Argument.ArgumentStrategy<PatternStrategyData> {

    private final SectionSpecifier specifier;

    public PatternLongArgumentStrategy() {
        specifier = new SectionSpecifier.Builder()
                .specifiedSection(LongArgument.class)
                .addRule(SectionSpecifier.Builder.Rules.DIGIT | SectionSpecifier.Builder.Rules.ONE_OR_MORE)
                .addRule(SectionSpecifier.Builder.Rules.CUSTOM, "lL")
                .build();
    }

    @Override
    public boolean isType(PatternStrategyData d) {
        Matcher matcher = d.getMatcher();
        matcher.usePattern(specifier.getPattern());
        matcher.reset(d.getText());
//        return matcher.find();
        return false;
    }
}
